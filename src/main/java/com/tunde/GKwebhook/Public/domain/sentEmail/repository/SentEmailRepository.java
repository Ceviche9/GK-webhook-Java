package com.tunde.GKwebhook.Public.domain.sentEmail.repository;

import com.tunde.GKwebhook.Public.domain.sentEmail.entity.SentEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SentEmailRepository extends JpaRepository<SentEmail, UUID> {
    Optional<SentEmail> findByEmail(String email);
}
