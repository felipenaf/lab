package com.example.inventoryApp.service;

import com.example.inventoryApp.messaging.Message;
import com.example.inventoryApp.messaging.MessageConsumer;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class InventoryConsumerService {
    private static final Logger log = LoggerFactory.getLogger(InventoryConsumerService.class);
    private static final String QUEUE = "inventory.queue";
    private final MessageConsumer consumer;

    public InventoryConsumerService(MessageConsumer consumer) {
        this.consumer = consumer;
    }

    @PostConstruct
    public void start() throws Exception {
        consumer.consume(QUEUE, "order.exchange", "order.created", this::consume);
        log.info("Waiting for messages...");
    }

    private void consume(Message message) {
        String json = new String(message.body(), StandardCharsets.UTF_8);

        log.info("Exchange: {}", message.exchange());
        log.info("Routing Key: {}", message.routingKey());
        log.info(json);
    }
}