package com.tunde.GKwebhook.Public.domain.sentEmail.controller;

import com.tunde.GKwebhook.Public.domain.sentEmail.dto.FindAllSentEmailResponseDTO;
import com.tunde.GKwebhook.Public.domain.sentEmail.service.SentEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/emails")
public class SentEmailController {

    @Autowired
    private SentEmailService sentEmailService;

    @Autowired
    private Environment env;

    @GetMapping
    public ResponseEntity<FindAllSentEmailResponseDTO> findAll(@RequestHeader("Authorization") String authorizationHeader) throws Exception {
        this.authenticate(authorizationHeader);
        return ResponseEntity.status(HttpStatus.OK).body(this.sentEmailService.findAll());
    }

    private void authenticate(String authorizationHeader) throws Exception {
        if (authorizationHeader != null) {
            var token = authorizationHeader.replace("Bearer", "").trim();
            if (!token.equals(this.env.getProperty("webhook.token"))) throw new Exception("Token is invalid!");
            return;
        }
        throw new Exception("Token is missing!");
    }

}
