package com.tunde.GKwebhook.Public.domain.sentEmail.dto;

import com.tunde.GKwebhook.Public.domain.sentEmail.entity.MethodType;

public record SentEmailDTO(
        String email,
        MethodType method,
        Boolean failed,
        String orderId
) {
}
