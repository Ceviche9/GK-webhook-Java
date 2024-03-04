package com.tunde.GKwebhook.Public.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tunde.GKwebhook.Public.sentEmail.dto.MethodType;
import com.tunde.GKwebhook.Public.sentEmail.dto.SentEmailDTO;
import com.tunde.GKwebhook.Public.sentEmail.entity.SentEmail;
import com.tunde.GKwebhook.Public.sentEmail.service.SentEmailService;
import com.tunde.GKwebhook.Public.order.dto.OrderDTO;
import com.tunde.GKwebhook.Public.order.dto.VerifyOrderDTO;
import com.tunde.GKwebhook.Public.infra.MailSenderProvider;
import com.tunde.GKwebhook.Public.infra.StoreProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public SentEmail sendValidationEmail(VerifyOrderDTO order) throws Exception {
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

        return sentEmail;
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
}
