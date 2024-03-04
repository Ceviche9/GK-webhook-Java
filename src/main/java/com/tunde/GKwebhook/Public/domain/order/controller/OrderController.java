package com.tunde.GKwebhook.Public.domain.order.controller;

import com.tunde.GKwebhook.Public.domain.sentEmail.dto.SentEmailResponseDTO;
import com.tunde.GKwebhook.Public.domain.order.dto.OrderDTO;
import com.tunde.GKwebhook.Public.domain.order.dto.VerifyOrderDTO;
import com.tunde.GKwebhook.Public.domain.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private Environment env;

    @Autowired
    private OrderService orderService;

    @GetMapping("/{orderId}")
    ResponseEntity<OrderDTO> findById(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("orderId") String id
    ) throws Exception {
        logger.info("Request Started.");
        this.authenticate(authorizationHeader);
        OrderDTO response = this.orderService.findById(id);
        logger.info("Sending response.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/send-email/{orderId}")
    ResponseEntity<SentEmailResponseDTO> sendEmail(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("orderId") String id
    ) throws Exception {
        logger.info("Request Started.");
        this.authenticate(authorizationHeader);
        var response = this.orderService.sendEmail(id);
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
        var response = this.orderService.sendValidationEmail(order);
        logger.info("Sending response.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private void authenticate(String authorizationHeader) throws Exception {
        if (authorizationHeader != null) {
            var token = authorizationHeader.replace("Bearer", "").trim();
            if (!token.equals(this.env.getProperty("webhook.token"))) {
                logger.error("Token is invalid: " + token);
                throw new Exception("Token is invalid!");
            };
            return;
        }
        logger.error("Token is missing!");
        throw new Exception("Token is missing!");
    }
}
