package com.example.orderApp.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OrderRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation
            .buildDefaultValidatorFactory()
            .getValidator();
    }

    @Test
    void shouldAcceptValidOrder() {
        OrderRequest request = new OrderRequest(
            "1",
            new BigDecimal("100.01")
        );

        Set<ConstraintViolation<OrderRequest>> violations =
            validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldRejectBlankCustomerId() {
        OrderRequest request = new OrderRequest(
            "",
            new BigDecimal("100.01")
        );

        Set<ConstraintViolation<OrderRequest>> violations =
            validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(
            "must not be blank",
            violations.iterator().next().getMessage()
        );
    }

    @Test
    void shouldRejectNullTotal() {
        OrderRequest request = new OrderRequest(
            "1",
            null
        );

        Set<ConstraintViolation<OrderRequest>> violations =
            validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(
            "must not be null",
            violations.iterator().next().getMessage()
        );
    }

    @Test
    void shouldRejectMoreThanTwoDecimalPlaces() {
        OrderRequest request = new OrderRequest(
            "1",
            new BigDecimal("100.011")
        );

        Set<ConstraintViolation<OrderRequest>> violations =
            validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(
            "must have at most 2 decimal places",
            violations.iterator().next().getMessage()
        );
    }
}