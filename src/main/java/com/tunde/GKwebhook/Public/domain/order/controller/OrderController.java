package com.tunde.GKwebhook.Public.domain.order.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tunde.GKwebhook.Public.domain.sentEmail.entity.SentEmail;
import com.tunde.GKwebhook.Public.domain.order.dto.OrderDTO;
import com.tunde.GKwebhook.Public.domain.order.dto.VerifyOrderDTO;
import com.tunde.GKwebhook.Public.domain.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/{orderId}")
    ResponseEntity<OrderDTO> findById(@PathVariable("orderId") String id) throws JsonProcessingException {
        OrderDTO response = this.orderService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/send-email")
    ResponseEntity<SentEmail> sendEmail(@RequestBody VerifyOrderDTO dto) throws Exception {
        SentEmail response = this.orderService.sendValidationEmail(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
