package com.tunde.GKwebhook.Public.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tunde.GKwebhook.Public.order.dto.OrderDTO;
import com.tunde.GKwebhook.Public.order.dto.VerifyOrderDTO;
import com.tunde.GKwebhook.Public.order.infra.MailSenderProvider;
import com.tunde.GKwebhook.Public.order.infra.StoreProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private StoreProvider storeProvider;

    @Autowired
    private MailSenderProvider mailSenderProvider;

    public OrderDTO findById(String id) throws JsonProcessingException {
        OrderDTO order = this.storeProvider.findOrderById(id);

        return order;
    }

    public int sendValidationEmail(VerifyOrderDTO order) throws Exception {
        this.verifyOrderStatus(order);

        this.mailSenderProvider.sendEmail(order);

        return order.numero();
    }

    private void verifyOrderStatus(VerifyOrderDTO order) throws Exception {
        if (!order.pagamentos().isEmpty() && !"mercadopagov1".equals(order.pagamentos().get(0).forma_pagamento().codigo())) {
            throw new Exception("Esse pedido não não foi pago no cartão.");
        }

        if (!order.situacao().aprovado()) {
            throw new Exception("Esse pedido ainda não foi aprovado!");
        }
    }
}
