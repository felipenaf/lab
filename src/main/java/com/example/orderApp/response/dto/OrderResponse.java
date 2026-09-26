package com.example.orderApp.response.dto;

import java.math.BigDecimal;

public record OrderResponse(String customerId, BigDecimal total) {
}
