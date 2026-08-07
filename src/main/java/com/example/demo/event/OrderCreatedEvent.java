package com.example.demo.event;

public record OrderCreatedEvent(String customerId, String total) {
}
