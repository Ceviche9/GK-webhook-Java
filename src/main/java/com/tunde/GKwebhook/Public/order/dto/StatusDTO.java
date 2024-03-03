package com.tunde.GKwebhook.Public.order.dto;

public record StatusDTO(
        boolean aprovado,
        boolean cancelado,
        String codigo,
        boolean final_,
        int id,
        String nome,
        boolean notificar_comprador,
        boolean padrao,
        String resource_uri
) {
}
