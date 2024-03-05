package com.tunde.GKwebhook.Public.domain.order.entity;

import com.tunde.GKwebhook.Public.domain.sentEmail.dto.SentEmailDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "sent_emails")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SentEmail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true)
    private String email;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime sended_at;

    private Boolean failed = false;

    @Enumerated(EnumType.STRING)
    private MethodType method = MethodType.manually;

    @Column(unique = true, name = "order_id")
    private String order_id;

    public SentEmail(SentEmailDTO dto) {
        this.email = dto.email();
        this.failed = dto.failed();
        this.method = dto.method();
        this.order_id = dto.orderId();
        this.setCurrentDate();
    }

    private void setCurrentDate() {
        ZoneId zonaBrasilia = ZoneId.of("America/Sao_Paulo");
        this.sended_at = ZonedDateTime.now(zonaBrasilia);
    }

}
