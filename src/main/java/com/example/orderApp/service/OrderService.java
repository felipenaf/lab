package com.example.orderApp.service;

import com.example.orderApp.entity.Order;
import com.example.orderApp.request.OrderRequest;
import com.example.orderApp.response.dto.OrderResponse;

import java.io.IOException;

public interface OrderService {
    void save(OrderRequest request) throws IOException;
    OrderResponse update(long id, OrderRequest request) throws IOException;
}
