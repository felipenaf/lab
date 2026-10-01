package com.example.inventoryApp.messaging;

public record Message(
    String exchange,
    String routingKey,
    byte[] body
) {}