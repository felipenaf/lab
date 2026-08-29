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
    private static final String QUEUE = "notification.queue";
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

        Channel channel = factory.newConnection().createChannel();
        // Guarantee the creation of exchange and queue if it doesn't exist
        //channel.exchangeDeclare("order.exchange", BuiltinExchangeType.DIRECT, true);
        channel.queueDeclare(QUEUE, true, false, false, null);

        // Guarantee the association of the queue with the exchange
        channel.queueBind(QUEUE, "order.exchange", "order.created");
        channel.basicConsume(QUEUE, false, this::consume, consumerTag -> {});

        System.out.println(this.getClass().getSimpleName() + " - Waiting for messages...");
    }

    private void consume(String consumerTag, Delivery delivery) {
        String json = new String(delivery.getBody(), StandardCharsets.UTF_8);

        System.out.println("------ " + this.getClass().getSimpleName() + " --------");
        System.out.println(
            "Deliver Tag: " + delivery.getEnvelope().getDeliveryTag()
                + " | Consumer Tag: " + consumerTag
                + " | Exchange: " + delivery.getEnvelope().getExchange()
                + " | Routing Key: " + delivery.getEnvelope().getRoutingKey()
                + " | Body: " + json
        );
        System.out.println("------------------------");

//        if (Objects.equals(delivery.getEnvelope().getRoutingKey(), "user.queue")) {
//            var user = (new ObjectMapper()).readValue(json, UserCreatedEvent.class);
//            System.out.println("Message Received: " + user);
//        }

//        channel.basicNack(
//            delivery.getEnvelope().getDeliveryTag(),
//            false,
//            true // Requeue the message
//        );
//
//        channel.basicAck(
//            delivery.getEnvelope().getDeliveryTag(),
//            false
//        );
    }
}