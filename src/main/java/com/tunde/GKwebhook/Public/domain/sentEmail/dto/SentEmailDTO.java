package com.tunde.GKwebhook.Public.domain.sentEmail.dto;

import com.tunde.GKwebhook.Public.domain.order.dto.VerifyOrderDTO;
import com.tunde.GKwebhook.Public.domain.order.entity.MethodType;
import com.tunde.GKwebhook.Public.domain.order.entity.SentEmail;

import java.util.UUID;

public record SentEmailDTO(
        String email,
        MethodType method,
        Boolean failed,
        String orderId
) {
    public static SentEmailDTO fromVerifyDTO(VerifyOrderDTO dto, Boolean failed) {
        return new SentEmailDTO(
                dto.cliente().email(),
                MethodType.webhook,
                failed,
                String.valueOf(dto.numero()
                )
        );
    }
}
