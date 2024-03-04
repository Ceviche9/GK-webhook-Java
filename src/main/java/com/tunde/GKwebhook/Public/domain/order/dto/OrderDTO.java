package com.tunde.GKwebhook.Public.domain.order.dto;

import java.util.List;

public record OrderDTO(
        int numero,
        ClientDTO cliente,
        List<ProductDTO> itens,
        List<PaymentDTO> pagamentos,
        StatusDTO situacao,
        String valor_total
) {
}
