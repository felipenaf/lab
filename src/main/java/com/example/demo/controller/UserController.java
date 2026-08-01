package com.example.demo.controller;

import com.example.demo.event.UserCreatedEvent;
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

    @PostMapping
    public ResponseEntity<User> save(@RequestBody User user){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        try {
//            To keep the message persisted when Rabbit goes down
//            it is necessary set true on second parameter of channel.queueDeclare
//            and set the property deliveryMode(2)

            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(
                "user.queue",
                true,   // durable
                false,  // exclusive
                false,  // autoDelete
                null
            );

            UserCreatedEvent event = new UserCreatedEvent(user.getName(), user.getEmail());
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(event);

            AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .contentType("application/json")
                .deliveryMode(2) // 2 = persistent
                .build();

            channel.basicPublish(
                "",
                "user.queue",
                props,
                json.getBytes(StandardCharsets.UTF_8)
            );

            System.out.println("------ Producer --------");
            System.out.println(json);
            System.out.println("Message published");
            System.out.println("------------------------");
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
