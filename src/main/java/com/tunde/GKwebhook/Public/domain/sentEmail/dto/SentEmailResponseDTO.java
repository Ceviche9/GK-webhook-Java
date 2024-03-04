package com.tunde.GKwebhook.Public.domain.sentEmail.dto;

import com.tunde.GKwebhook.Public.domain.sentEmail.entity.MethodType;
import lombok.NoArgsConstructor;

public record SentEmailResponseDTO(
        String email,
        MethodType method,
        Boolean failed,
        String orderId,
        String sentAt
) {
}
