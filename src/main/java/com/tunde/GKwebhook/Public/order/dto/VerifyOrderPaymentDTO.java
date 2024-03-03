package com.tunde.GKwebhook.Public.order.dto;

public record VerifyOrderPaymentDTO(
        int id,
        int numero_parcelas,
        Double valor,
        PaymentTypeDTO forma_pagamento
) {
}
