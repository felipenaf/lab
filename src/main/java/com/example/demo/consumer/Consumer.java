package com.example.demo.consumer;

import com.example.demo.event.UserCreatedEvent;
import com.example.demo.properties.RabbitmqProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
public class Consumer {

    private final RabbitmqProperties env;

    public Consumer(RabbitmqProperties env) {
        this.env = env;
    }

    @PostConstruct
    public void start() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(env.getHost());
        factory.setPort(env.getPort());
        factory.setUsername(env.getUsername());
        factory.setPassword(env.getPassword());

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare("user.queue", true, false, false, null);

        DeliverCallback callback = (consumerTag, delivery) -> {
            String json = new String(delivery.getBody(), StandardCharsets.UTF_8);

            System.out.println("------ Consumer --------");
            System.out.println("consumerTag: " + consumerTag);
            System.out.println("Routing Key: " + delivery.getEnvelope().getRoutingKey());

            if (Objects.equals(delivery.getEnvelope().getRoutingKey(), "user.queue")) {
                var user = (new ObjectMapper()).readValue(json, UserCreatedEvent.class);
                System.out.println("Message Received: " + user);
            }

            System.out.println("------------------------");

//            channel.basicNack(
//                delivery.getEnvelope().getDeliveryTag(),
//                false,
//                true // Requeue the message
//            );

//            channel.basicAck(
//                delivery.getEnvelope().getDeliveryTag(),
//                false
//            );
        };

        channel.basicConsume(
            "user.queue",
            false,   // autoAck
            callback,
            consumerTag -> {}
        );

        System.out.println("Waiting for messages...");
    }
}