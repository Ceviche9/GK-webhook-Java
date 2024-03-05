package com.tunde.GKwebhook.Public.domain.sentEmail.controller;

import com.tunde.GKwebhook.Public.domain.order.controller.OrderController;
import com.tunde.GKwebhook.Public.domain.order.dto.VerifyOrderDTO;
import com.tunde.GKwebhook.Public.domain.sentEmail.dto.FindAllSentEmailResponseDTO;
import com.tunde.GKwebhook.Public.domain.sentEmail.dto.SentEmailResponseDTO;
import com.tunde.GKwebhook.Public.domain.sentEmail.service.SentEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emails")
public class SentEmailController {

    private static final Logger logger = LoggerFactory.getLogger(SentEmailController.class);

    @Autowired
    private SentEmailService sentEmailService;

    @Autowired
    private Environment env;

    @GetMapping
    public ResponseEntity<FindAllSentEmailResponseDTO> findAll(@RequestHeader("Authorization") String authorizationHeader) throws Exception {
        logger.info("Request Started.");
        this.authenticate(authorizationHeader);
        logger.info("Sending response.");
        return ResponseEntity.status(HttpStatus.OK).body(this.sentEmailService.findAll());
    }

    @PostMapping("/{orderId}")
    ResponseEntity<SentEmailResponseDTO> sendEmail(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("orderId") String id
    ) throws Exception {
        logger.info("Request Started.");
        this.authenticate(authorizationHeader);
        var response = this.sentEmailService.sendEmail(id);
        logger.info("Sending response.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/webhook")
    ResponseEntity<SentEmailResponseDTO> webhook(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody VerifyOrderDTO order
    ) throws Exception {
        logger.info("Request Started [WEBHOOK].");
        this.authenticate(authorizationHeader);
        var response = this.sentEmailService.sendValidationEmail(order);
        logger.info("Sending response.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
