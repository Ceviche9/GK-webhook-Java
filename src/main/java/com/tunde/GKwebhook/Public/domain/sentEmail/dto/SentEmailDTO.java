package com.tunde.GKwebhook.Public.domain.sentEmail.dto;

public record SentEmailDTO(
        String email,
        MethodType method,
        Boolean failed,
        String orderId
) {
}
