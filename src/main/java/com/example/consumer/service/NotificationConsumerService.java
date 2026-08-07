package com.example.consumer.service;

import com.example.consumer.event.UserCreatedEvent;
import com.example.consumer.properties.RabbitmqProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
public class NotificationConsumerService {

    private final RabbitmqProperties env;

    public NotificationConsumerService(RabbitmqProperties env) {
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

        channel.exchangeDeclare(
            "order.exchange",
            BuiltinExchangeType.DIRECT,
            true // durable
        );

        channel.queueDeclare(
            "notification.queue",
            true,
            false,
            false,
            null
        );

        channel.queueBind(
            "notification.queue",
            "order.exchange",
            "order.created"
        );

        DeliverCallback callback = (consumerTag, delivery) -> {
            String json = new String(delivery.getBody(), StandardCharsets.UTF_8);

            System.out.println("------ " + this.getClass().getSimpleName() + " --------");
            System.out.println("consumerTag: " + consumerTag);
            System.out.println("Exchange: " + delivery.getEnvelope().getExchange());
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
            "notification.queue",
            false, // autoAck
            callback,
            consumerTag -> {}
        );

        System.out.println("Waiting for messages...");
    }
}