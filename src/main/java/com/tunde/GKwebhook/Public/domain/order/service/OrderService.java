package com.tunde.GKwebhook.Public.domain.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tunde.GKwebhook.Public.domain.order.controller.OrderController;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private static Logger logger = LoggerFactory.getLogger(OrderService.class);

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
        logger.info("Indo buscar o pedido na api");
        var order = this.storeProvider.findOrderById(id);
        logger.info("Pedido encontrado");
        var verifyOrderDTO = this.createVerifyOrderDTO(order);
        logger.info("DTO criado");
        return this.sendValidationEmail(verifyOrderDTO);
    }

    public SentEmailResponseDTO sendValidationEmail(VerifyOrderDTO order) throws Exception {
        this.verifyOrderStatus(order);
        SentEmail sentEmail = null;
        logger.info("Verificando se o email já está salvo no banco");
        Boolean alreadySent = this.sentEmailService.alreadySent(order.cliente().email(), String.valueOf(order.numero()));
        if (alreadySent) {
            logger.error("Esse já existe no banco");
            throw new Exception("Já foi enviado um email para esse cliente");
        }

        try {
            logger.info("Enviando dados do pedido para o provider de envio de emails");
            this.mailSenderProvider.sendEmail(order);
            SentEmailDTO sentEmailDTO = this.createSentEmailDTO(order, false);
            logger.info("Email enviado");
            sentEmail = this.sentEmailService.saveEmail(sentEmailDTO);
            logger.info("Email salvo no banco: " + sentEmail.getId());
        } catch (Exception err) {
            logger.info("Não foi possível enviar o email");
            SentEmailDTO sentEmailDTO = this.createSentEmailDTO(order, true);
            sentEmail = this.sentEmailService.saveEmail(sentEmailDTO);
            logger.error("Tentativa de envio salva no banco: " + sentEmail.getId());
        }

        logger.info("Retornando resposta");
        return this.sentEmailService.createSentEmailResponseDTO(sentEmail);
    }

    private void verifyOrderStatus(VerifyOrderDTO order) throws Exception {
        logger.info("Verificando status do pedido");
        if (!order.pagamentos().isEmpty() && !"mercadopagov1".equals(order.pagamentos().get(0).forma_pagamento().codigo())) {
            logger.error("Pedido não foi pago no cartão: "+ order.pagamentos().get(0).forma_pagamento().codigo());
            throw new Exception("Esse pedido não não foi pago no cartão.");
        }

        if (!order.situacao().aprovado()) {
            logger.error("Pedido não foi aprovado: "+ order.numero());
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
