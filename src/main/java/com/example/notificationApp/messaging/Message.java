package com.example.notificationApp.messaging;

public record Message(
    String exchange,
    String routingKey,
    byte[] body
) {}