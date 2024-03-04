package com.tunde.GKwebhook.Public.sentEmail.dto;

public record SentEmailDTO(
        String email,
        MethodType method,
        Boolean failed,
        String orderId
) {
}
