package com.tunde.GKwebhook.Public.order.dto;

import java.util.List;

public record VerifyOrderDTO(
        ClientDTO cliente,
        List<ProductDTO> itens,
        int numero,
        StatusDTO situacao,
        List<VerifyOrderPaymentDTO>  pagamentos
) {
}
