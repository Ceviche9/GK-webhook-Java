package com.tunde.GKwebhook.Public.domain.sentEmail.dto;

import com.tunde.GKwebhook.Public.domain.order.dto.VerifyOrderDTO;
import com.tunde.GKwebhook.Public.domain.order.entity.MethodType;

public record SentEmailDTO(
        String email,
        MethodType methodType,
        Boolean failed,
        String orderId
) {
    public static SentEmailDTO fromVerifyDTO(VerifyOrderDTO dto, Boolean failed) {
        return new SentEmailDTO(
                dto.cliente().email(),
                dto.methodType() == null ? MethodType.webhook : dto.methodType(),
                failed,
                String.valueOf(dto.numero()
                )
        );
    }
}
