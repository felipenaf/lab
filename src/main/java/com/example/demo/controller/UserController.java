package com.example.demo.controller;

import com.rabbitmq.client.Channel;
import com.example.demo.entity.User;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping
    public ResponseEntity<User> save(@RequestBody User user){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            System.out.println(channel);
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
