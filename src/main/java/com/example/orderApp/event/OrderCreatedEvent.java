package com.example.orderApp.event;

public record OrderCreatedEvent(String customerId, String total) {
}
