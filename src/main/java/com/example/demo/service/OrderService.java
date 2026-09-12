package com.example.demo.service;

import com.example.demo.messaging.MessagePublisher;
import com.example.demo.entity.Order;
import com.example.demo.event.OrderCreatedEvent;
import com.example.demo.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class OrderService {
    private static final String EXCHANGE = "order.exchange";
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final MessagePublisher messagePublisher;
    private final OrderRepository orderRepository;

    public OrderService(MessagePublisher messagePublisher, OrderRepository orderRepository) {
        this.messagePublisher = messagePublisher;
        this.orderRepository = orderRepository;
    }

    public void save(@RequestBody Order order) throws IOException {
        String json = getJson(order);
//        TODO: I need create a transaction to assure the message will sent just if the database save correctly
        messagePublisher.publish(EXCHANGE, "order.created", json.getBytes(StandardCharsets.UTF_8));
        log.info("Message: " + json);
        orderRepository.save(order);
    }

    public void update(@RequestBody Order order) throws IOException {
        String json = getJson(order);
        messagePublisher.publish(EXCHANGE, "order.updated", json.getBytes(StandardCharsets.UTF_8));
        log.info("Message: " + json);
    }

    private static String getJson(Order order) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(
            new OrderCreatedEvent(order.getCustomerId(), order.getTotal())
        );
    }
}
