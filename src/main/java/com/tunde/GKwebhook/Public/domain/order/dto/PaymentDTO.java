package com.tunde.GKwebhook.Public.domain.order.dto;

public record PaymentDTO(
        PaymentTypeDTO forma_pagamento,
        int id,
        String pagamento_tipo,
        InstalmentsDTO parcelamento,
        String valor,
        String valor_pago
) {
}
