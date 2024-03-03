package com.tunde.GKwebhook.Public.order.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tunde.GKwebhook.Public.order.dto.OrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class StoreProvider {
    @Autowired
    private Environment env;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public OrderDTO findOrderById(String id) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", this.env.getProperty("auth"));
        headers.set("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> orderResponse = this.restTemplate.exchange(
                this.env.getProperty("store.api.url") + "/pedido/" + id,
                HttpMethod.GET,
                entity,
                String.class
        );

        JsonNode jsonNode = this.objectMapper.readTree(orderResponse.getBody());
        return this.objectMapper.treeToValue(jsonNode, OrderDTO.class);
    }

}
