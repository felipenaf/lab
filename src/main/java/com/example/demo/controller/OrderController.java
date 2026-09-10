package com.example.demo.controller;

import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import com.example.demo.event.OrderCreatedEvent;
import com.example.demo.event.UserCreatedEvent;
import com.example.demo.properties.RabbitmqProperties;
import com.example.demo.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/order")
public class OrderController {
    private static final Logger log =
        LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> save(@RequestBody Order order){
        try {
            orderService.save(order);
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (IOException | TimeoutException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(order, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping
    public ResponseEntity<Order> update(@RequestBody Order order){
        try {
            orderService.update(order);
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (IOException | TimeoutException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(order, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
