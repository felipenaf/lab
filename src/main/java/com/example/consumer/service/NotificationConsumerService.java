package com.example.consumer.service;

import com.example.consumer.messaging.Message;
import com.example.consumer.messaging.MessageConsumer;
import com.rabbitmq.client.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class NotificationConsumerService {
    private static final String QUEUE = "notification.queue";
    private final MessageConsumer consumer;

    public NotificationConsumerService(MessageConsumer consumer) {
        this.consumer = consumer;
    }

    @PostConstruct
    public void start() throws Exception {
        consumer.consume(QUEUE, "order.exchange", "order.*", this::consume);
        System.out.println(this.getClass().getSimpleName() + " - Waiting for messages...");
    }

    private void consume(Message message) {
        String json = new String(message.body(), StandardCharsets.UTF_8);

        System.out.println("-- " + this.getClass().getSimpleName() + " --");
        System.out.println("Exchange: " + message.exchange());
        System.out.println("Routing Key: " + message.routingKey());
        System.out.println("Body: " + json);
        System.out.println("--------------------------");
    }
}