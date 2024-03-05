package com.tunde.GKwebhook.Public.domain.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tunde.GKwebhook.Public.domain.sentEmail.service.SentEmailService;
import com.tunde.GKwebhook.Public.domain.order.dto.OrderDTO;
import com.tunde.GKwebhook.Public.domain.order.dto.VerifyOrderDTO;
import com.tunde.GKwebhook.Public.infra.providers.MailSenderProvider;
import com.tunde.GKwebhook.Public.infra.providers.StoreProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


     public void verifyOrderStatus(VerifyOrderDTO order) throws Exception {
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
