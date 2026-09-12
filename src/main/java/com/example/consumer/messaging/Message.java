package com.example.consumer.messaging;

public record Message(
    String exchange,
    String routingKey,
    byte[] body
) {}