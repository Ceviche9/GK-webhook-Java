package com.tunde.GKwebhook.Public.domain.order.dto;

import com.tunde.GKwebhook.Public.domain.order.entity.MethodType;

import java.util.List;
import java.util.Optional;

public record VerifyOrderDTO(
        ClientDTO cliente,
        List<ProductDTO> itens,
        int numero,
        StatusDTO situacao,
        List<VerifyOrderPaymentDTO>  pagamentos,
        MethodType methodType
) {
    public static VerifyOrderDTO fromOrderDTO(OrderDTO dto, Optional<MethodType> methodType) {
        var verifyOrderPaymentDTO = new VerifyOrderPaymentDTO(
                dto.pagamentos().get(0).id(),
                dto.pagamentos().get(0).parcelamento().numero_parcelas(),
                dto.pagamentos().get(0).parcelamento().valor_parcela(),
                dto.pagamentos().get(0).valor(),
                dto.pagamentos().get(0).forma_pagamento()
        );
        return new VerifyOrderDTO(
                dto.cliente(),
                dto.itens(),
                dto.numero(),
                dto.situacao(),
                List.of(verifyOrderPaymentDTO),
                methodType.orElse(MethodType.webhook)
        );
    }

    public static VerifyOrderDTO setMethodType(VerifyOrderDTO dto,MethodType methodType) {
        return new VerifyOrderDTO(
                dto.cliente(),
                dto.itens(),
                dto.numero(),
                dto.situacao(),
                dto.pagamentos(),
                methodType
        );
    }

}
