package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.event.UserCreatedEvent;
import com.example.demo.event.UserCreatedEvent;
import com.example.demo.properties.RabbitmqProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import com.example.demo.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/user")
public class UserController {
    private static final int PERSISTENT = 2;
    private final RabbitmqProperties env;

    public UserController(RabbitmqProperties env) {
        this.env = env;
    }

    @PostMapping
    public ResponseEntity<User> save(@RequestBody User user){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(env.getHost());
        factory.setPort(env.getPort());
        factory.setUsername(env.getUsername());
        factory.setPassword(env.getPassword());

        try {
            Channel channel = factory.newConnection().createChannel();
            channel.exchangeDeclare("user.exchange", BuiltinExchangeType.TOPIC, true);

            AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .contentType("application/json")
                .deliveryMode(PERSISTENT)
                .build();

            String json = getJson(user);

            channel.basicPublish(
                "user.exchange",
                "user.created",
                props,
                json.getBytes(StandardCharsets.UTF_8)
            );

            System.out.println("-- User Producer - Message published --");
            System.out.println(json);
            System.out.println("----------------------------------------");
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    private static String getJson(User user) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(
            new UserCreatedEvent(user.getName(), user.getEmail())
        );
    }
}
