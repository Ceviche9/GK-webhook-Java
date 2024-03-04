package com.tunde.GKwebhook.Public.domain.sentEmail.dto;

import java.util.List;

public record FindAllSentEmailResponseDTO(
        List<SentEmailResponseDTO> sent,
        Integer emailsSent
) {
}
