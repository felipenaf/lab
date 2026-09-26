package com.example.orderApp.controller;

import com.example.orderApp.entity.Order;
import com.example.orderApp.request.OrderRequest;
import com.example.orderApp.response.SuccessResponse;
import com.example.orderApp.response.dto.OrderResponse;
import com.example.orderApp.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/order")
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody OrderRequest request){
        try {
            orderService.save(request);
            return ResponseEntity.ok(new SuccessResponse("OK"));
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrderResponse> update(@PathVariable long id, @RequestBody OrderRequest request){
        try {
            var response = orderService.update(id, request);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
