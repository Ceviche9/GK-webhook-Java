package com.tunde.GKwebhook.Public.domain.sentEmail.service;

import com.tunde.GKwebhook.Public.domain.sentEmail.dto.FindAllSentEmailResponseDTO;
import com.tunde.GKwebhook.Public.domain.sentEmail.dto.SentEmailDTO;
import com.tunde.GKwebhook.Public.domain.sentEmail.dto.SentEmailResponseDTO;
import com.tunde.GKwebhook.Public.domain.sentEmail.entity.SentEmail;
import com.tunde.GKwebhook.Public.domain.sentEmail.repository.SentEmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SentEmailService {

    @Autowired
    private SentEmailRepository repository;


    public SentEmail saveEmail(SentEmailDTO dto) {
        SentEmail newSentEmail = new SentEmail(dto);
        return this.repository.save(newSentEmail);
    }

    public Boolean alreadySent(String email, String orderId) {
        Optional<SentEmail> sentEmail = this.repository.findByEmail(email);
        Optional<SentEmail> sentEmailByOrder = this.repository.findByOrderId(orderId);
        if (
                sentEmail.isPresent() &&
                !sentEmail.get().getFailed() ||
                sentEmailByOrder.isPresent()
        ) {
            return true;
        } else return false;
    }

    public FindAllSentEmailResponseDTO findAll() {
        List<SentEmail> emails = this.repository.findAll();
        List<SentEmailResponseDTO> responseArray = new ArrayList<>();

        for (SentEmail email : emails) {
            responseArray.add(this.createSentEmailResponseDTO(email));
        }

        FindAllSentEmailResponseDTO response = new FindAllSentEmailResponseDTO(
                responseArray,
                responseArray.size()
        );

        return response;
    }

    public SentEmailResponseDTO createSentEmailResponseDTO(SentEmail email) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return  new SentEmailResponseDTO(
                email.getEmail(),
                email.getMethod(),
                email.getFailed(),
                email.getOrder_id(),
                email.getSended_at().format(formatter)
        );
    }
}
