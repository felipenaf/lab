package com.example.orderApp.event;

import java.math.BigDecimal;

public record OrderCreatedEvent(String customerId, BigDecimal total) {
}
