package com.tunde.GKwebhook.Public.domain.sentEmail.repository;

import com.tunde.GKwebhook.Public.domain.order.entity.SentEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SentEmailRepository extends JpaRepository<SentEmail, UUID> {
    Optional<SentEmail> findByEmail(String email);

    @Query(value = "SELECT * FROM sent_emails WHERE order_id = :orderId", nativeQuery = true)
    Optional<SentEmail> findByOrderId(String orderId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE SentEmail se SET se.failed = :failed WHERE se.id = :sentEmailId")
    void updateStatusById(boolean failed, UUID sentEmailId);
}
