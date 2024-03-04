package com.tunde.GKwebhook.Public.domain.sentEmail.service;

import com.tunde.GKwebhook.Public.domain.sentEmail.dto.SentEmailDTO;
import com.tunde.GKwebhook.Public.domain.sentEmail.entity.SentEmail;
import com.tunde.GKwebhook.Public.domain.sentEmail.repository.SentEmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SentEmailService {

    @Autowired
    private SentEmailRepository repository;


    public SentEmail saveEmail(SentEmailDTO dto) {
        SentEmail newSentEmail = new SentEmail(dto);
        return this.repository.save(newSentEmail);
    }

    public Boolean alreadySent(String email) {
        Optional<SentEmail> sentEmail = this.repository.findByEmail(email);
        return sentEmail.isPresent();
    }

}
