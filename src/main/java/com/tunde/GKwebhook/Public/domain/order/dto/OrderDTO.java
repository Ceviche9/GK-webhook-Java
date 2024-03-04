package com.tunde.GKwebhook.Public.domain.order.dto;

public record OrderDTO(
        int numero,
        ClientDTO cliente,
        ProductDTO[] itens,
        PaymentDTO[] pagamentos,
        StatusDTO situacao,
        String valor_total
) {
}
