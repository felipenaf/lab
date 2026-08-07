package com.example.demo.controller;

import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import com.example.demo.event.OrderCreatedEvent;
import com.example.demo.event.UserCreatedEvent;
import com.example.demo.properties.RabbitmqProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final RabbitmqProperties env;

    public OrderController(RabbitmqProperties env) {
        this.env = env;
    }

    @PostMapping
    public ResponseEntity<Order> save(@RequestBody Order order){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(env.getHost());
        factory.setPort(env.getPort());
        factory.setUsername(env.getUsername());
        factory.setPassword(env.getPassword());

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(
                "order.exchange",
                BuiltinExchangeType.DIRECT,
                true // durable
            );

//            channel.queueDeclare(
//                "user.queue",
//                true,   // durable
//                false,  // exclusive
//                false,  // autoDelete
//                null
//            );

            String json = new ObjectMapper().writeValueAsString(
                new OrderCreatedEvent(order.getCustomerId(), order.getTotal())
            );

            AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .contentType("application/json")
                .deliveryMode(2) // 2 = persistent
                .build();

            channel.basicPublish(
                "order.exchange",      // exchange
                "order.created",       // routing key
                props,
                json.getBytes(StandardCharsets.UTF_8)
            );

            System.out.println("------ Order Producer --------");
            System.out.println(json);
            System.out.println("Message published");
            System.out.println("------------------------");
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}
