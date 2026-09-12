package com.example.demo.service;

import com.example.demo.messaging.MessagePublisher;
import com.example.demo.entity.Order;
import com.example.demo.event.OrderCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class OrderService {
    private static final String EXCHANGE = "order.exchange";
    private final MessagePublisher messagePublisher;

    public OrderService(MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    public void save(@RequestBody Order order) throws IOException {
        String json = getJson(order);
        messagePublisher.publish(EXCHANGE, "order.created", json.getBytes(StandardCharsets.UTF_8));

        System.out.println("-- Order Producer - Message published --");
        System.out.println(json);
        System.out.println("----------------------------------------");
    }

    public void update(@RequestBody Order order) throws IOException {
        String json = getJson(order);
        messagePublisher.publish(EXCHANGE, "order.updated", json.getBytes(StandardCharsets.UTF_8));

        System.out.println("-- Order Producer - Message published --");
        System.out.println(json);
        System.out.println("----------------------------------------");
    }

    private static String getJson(Order order) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(
            new OrderCreatedEvent(order.getCustomerId(), order.getTotal())
        );
    }
}
