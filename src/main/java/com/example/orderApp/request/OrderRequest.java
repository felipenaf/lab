package com.example.orderApp.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OrderRequest(
    @NotBlank
    String customerId,

    @NotNull
    @Digits(integer = 10, fraction = 2, message = "must have at most 2 decimal places")
    BigDecimal total
) {
}
