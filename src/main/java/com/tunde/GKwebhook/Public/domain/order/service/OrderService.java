package com.tunde.GKwebhook.Public.domain.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tunde.GKwebhook.Public.domain.order.dto.VerifyOrderPaymentDTO;
import com.tunde.GKwebhook.Public.domain.sentEmail.dto.SentEmailResponseDTO;
import com.tunde.GKwebhook.Public.domain.sentEmail.entity.MethodType;
import com.tunde.GKwebhook.Public.domain.sentEmail.dto.SentEmailDTO;
import com.tunde.GKwebhook.Public.domain.sentEmail.entity.SentEmail;
import com.tunde.GKwebhook.Public.domain.sentEmail.service.SentEmailService;
import com.tunde.GKwebhook.Public.domain.order.dto.OrderDTO;
import com.tunde.GKwebhook.Public.domain.order.dto.VerifyOrderDTO;
import com.tunde.GKwebhook.Public.infra.MailSenderProvider;
import com.tunde.GKwebhook.Public.infra.StoreProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private StoreProvider storeProvider;

    @Autowired
    private MailSenderProvider mailSenderProvider;

    @Autowired
    private SentEmailService sentEmailService;

    public OrderDTO findById(String id) throws JsonProcessingException {
        OrderDTO order = this.storeProvider.findOrderById(id);
        return order;
    }

    public SentEmailResponseDTO sendEmail(String id) throws Exception {
        var order = this.storeProvider.findOrderById(id);
        var verifyOrderDTO = this.createVerifyOrderDTO(order);
        return this.sendValidationEmail(verifyOrderDTO);
    }

    public SentEmailResponseDTO sendValidationEmail(VerifyOrderDTO order) throws Exception {
        this.verifyOrderStatus(order);
        SentEmail sentEmail = null;
        Boolean alreadySent = this.sentEmailService.alreadySent(order.cliente().email());
        if (alreadySent) throw new Exception("Já foi enviado um email para esse cliente");

        try {
            this.mailSenderProvider.sendEmail(order);
            SentEmailDTO sentEmailDTO = this.createSentEmailDTO(order, false);
            sentEmail = this.sentEmailService.saveEmail(sentEmailDTO);
        } catch (Exception err) {
            SentEmailDTO sentEmailDTO = this.createSentEmailDTO(order, true);
            sentEmail = this.sentEmailService.saveEmail(sentEmailDTO);
        }

        return this.sentEmailService.createSentEmailResponseDTO(sentEmail);
    }

    private void verifyOrderStatus(VerifyOrderDTO order) throws Exception {
        if (!order.pagamentos().isEmpty() && !"mercadopagov1".equals(order.pagamentos().get(0).forma_pagamento().codigo())) {
            throw new Exception("Esse pedido não não foi pago no cartão.");
        }

        if (!order.situacao().aprovado()) {
            throw new Exception("Esse pedido ainda não foi aprovado!");
        }
    }

    private SentEmailDTO createSentEmailDTO(VerifyOrderDTO dto, Boolean failed) {
        SentEmailDTO sentEmailDTO = new SentEmailDTO(
                dto.cliente().email(),
                MethodType.webhook,
                failed,
                String.valueOf(dto.numero()
                )
        );

        return sentEmailDTO;
    }

    private VerifyOrderDTO createVerifyOrderDTO(OrderDTO order) {
        var verifyOrderPaymentDTO = new VerifyOrderPaymentDTO(
                order.pagamentos().get(0).id(),
                order.pagamentos().get(0).parcelamento().numero_parcelas(),
                order.pagamentos().get(0).parcelamento().valor_parcela(),
                order.pagamentos().get(0).valor(),
                order.pagamentos().get(0).forma_pagamento()
        );
        return new VerifyOrderDTO(
                order.cliente(),
                order.itens(),
                order.numero(),
                order.situacao(),
                List.of(verifyOrderPaymentDTO)
        );
    }
}
