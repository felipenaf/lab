package com.example.consumer.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbitmq")
public record RabbitmqProperties(
    String host,
    Integer port,
    String username,
    String password
) {}