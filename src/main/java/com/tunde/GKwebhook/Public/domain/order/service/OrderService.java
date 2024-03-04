package com.tunde.GKwebhook.Public.domain.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tunde.GKwebhook.Public.domain.order.dto.VerifyOrderPaymentDTO;
import com.tunde.GKwebhook.Public.domain.sentEmail.dto.SentEmailResponseDTO;
import com.tunde.GKwebhook.Public.domain.order.entity.MethodType;
import com.tunde.GKwebhook.Public.domain.sentEmail.dto.SentEmailDTO;
import com.tunde.GKwebhook.Public.domain.order.entity.SentEmail;
import com.tunde.GKwebhook.Public.domain.sentEmail.service.SentEmailService;
import com.tunde.GKwebhook.Public.domain.order.dto.OrderDTO;
import com.tunde.GKwebhook.Public.domain.order.dto.VerifyOrderDTO;
import com.tunde.GKwebhook.Public.infra.providers.MailSenderProvider;
import com.tunde.GKwebhook.Public.infra.providers.StoreProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private StoreProvider storeProvider;

    @Autowired
    private MailSenderProvider mailSenderProvider;

    @Autowired
    private SentEmailService sentEmailService;

    public OrderDTO findById(String id) throws JsonProcessingException {
        return this.storeProvider.findOrderById(id);
    }

    public SentEmailResponseDTO sendEmail(String id) throws Exception {
        var order = this.storeProvider.findOrderById(id);
        logger.info("Order found: " + order.numero());
        var verifyOrderDTO = VerifyOrderDTO.fromOrderDTO(order);
        logger.info("DTO created");
        return this.sendValidationEmail(verifyOrderDTO);
    }

    public SentEmailResponseDTO sendValidationEmail(VerifyOrderDTO order) throws Exception {
        this.verifyOrderStatus(order);
        SentEmail sentEmail = null;
        logger.info("Check if email or orderId is already stored");
        Boolean alreadySent = this.sentEmailService.alreadySent(order.cliente().email(), String.valueOf(order.numero()));
        if (alreadySent) {
            logger.error("OrderId or Email already in the DB: OrderId: "+ order.numero() + ".Email: " + order.cliente().email());
            throw new Exception("Já foi enviado um email para esse cliente ou esse pedido já está cadastrado no banco.");
        }

        try {
            logger.info("Calling mail sender provider");
            this.mailSenderProvider.sendEmail(order);
            SentEmailDTO sentEmailDTO = SentEmailDTO.fromVerifyDTO(order, false);
            logger.info("Email sent");
            sentEmail = this.sentEmailService.saveEmail(sentEmailDTO);
            logger.info("Email stored in DB: " + sentEmail.getId());
        } catch (Exception err) {
            logger.info("Error while sent email");
            SentEmailDTO sentEmailDTO = SentEmailDTO.fromVerifyDTO(order, true);
            sentEmail = this.sentEmailService.saveEmail(sentEmailDTO);
            logger.error("Error saved in DB: " + sentEmail.getId());
        }

        return this.sentEmailService.createSentEmailResponseDTO(sentEmail);
    }

    private void verifyOrderStatus(VerifyOrderDTO order) throws Exception {
        logger.info("Check order status");
        if (!order.pagamentos().isEmpty() && !"mercadopagov1".equals(order.pagamentos().get(0).forma_pagamento().codigo())) {
            logger.error("This order was not payed with credit card: "+ order.pagamentos().get(0).forma_pagamento().codigo());
            throw new Exception("Esse pedido não não foi pago no cartão.");
        }

        if (!order.situacao().aprovado()) {
            logger.error("Order not approved: "+ order.numero());
            throw new Exception("Esse pedido ainda não foi aprovado!");
        }
    }
}
