package com.tunde.GKwebhook.Public.domain.order.dto;

import java.util.List;

public record VerifyOrderDTO(
        ClientDTO cliente,
        List<ProductDTO> itens,
        int numero,
        StatusDTO situacao,
        List<VerifyOrderPaymentDTO>  pagamentos
) {

    public static VerifyOrderDTO fromOrderDTO(OrderDTO dto) {
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
                List.of(verifyOrderPaymentDTO)
        );
    }
}
