package com.example.demo.controller;

import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import com.example.demo.event.OrderCreatedEvent;
import com.example.demo.event.UserCreatedEvent;
import com.example.demo.properties.RabbitmqProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/order")
public class OrderController {
    private static final int PERSISTENT = 2;
    private static final String EXCHANGE = "order.exchange";
    private final AMQP.BasicProperties props;
    private final ConnectionFactory connectionFactory;

    public OrderController(RabbitmqProperties env) {
        this.props = new AMQP.BasicProperties.Builder()
            .contentType("application/json")
            .deliveryMode(PERSISTENT)
            .build();

        this.connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(env.getHost());
        connectionFactory.setPort(env.getPort());
        connectionFactory.setUsername(env.getUsername());
        connectionFactory.setPassword(env.getPassword());
    }

    @PostMapping
    public ResponseEntity<Order> save(@RequestBody Order order){
        try {
            Channel channel = connectionFactory.newConnection().createChannel();
            channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.TOPIC, true);

            String json = getJson(order);

            channel.basicPublish(
                EXCHANGE,
                "order.created",
                props,
                json.getBytes(StandardCharsets.UTF_8)
            );

            System.out.println("-- Order Producer - Message published --");
            System.out.println(json);
            System.out.println("----------------------------------------");
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<Order> update(@RequestBody Order order){
        try {
            Channel channel = connectionFactory.newConnection().createChannel();
            channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.TOPIC, true);

            String json = getJson(order);

            channel.basicPublish(
                EXCHANGE,
                "order.updated",
                props,
                json.getBytes(StandardCharsets.UTF_8)
            );

            System.out.println("-- Order Producer - Message published --");
            System.out.println(json);
            System.out.println("----------------------------------------");
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    private static String getJson(Order order) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(
            new OrderCreatedEvent(order.getCustomerId(), order.getTotal())
        );
    }
}
