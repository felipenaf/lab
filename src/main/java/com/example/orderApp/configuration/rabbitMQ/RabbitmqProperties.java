package com.example.orderApp.configuration.rabbitMQ;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "rabbitmq")
public record RabbitmqProperties(
    String host,
    Integer port,
    String username,
    String password,
    List<QueueProperties> queues
) {
    public record QueueProperties(
        String queue,
        String exchangeName,
        String exchangeType,
        String routingKey
    ) {}
}
