package com.tunde.GKwebhook.Public.domain.order.dto;

public record VerifyOrderPaymentDTO(
        int id,
        int numero_parcelas,
        Double valor_parcela,
        String valor,
        PaymentTypeDTO forma_pagamento
) {
}
