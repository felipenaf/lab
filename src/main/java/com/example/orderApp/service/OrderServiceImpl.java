package com.example.orderApp.service;

import com.example.orderApp.entity.OutboxEvent;
import com.example.orderApp.entity.Order;
import com.example.orderApp.event.OrderCreatedEvent;
import com.example.orderApp.repository.OrderRepository;
import com.example.orderApp.repository.OutboxEventRepository;
import com.example.orderApp.request.OrderRequest;
import com.example.orderApp.response.dto.OrderResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
public class OrderServiceImpl implements OrderService {
    private static final String EXCHANGE = "order.exchange";
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;

    public OrderServiceImpl(
        OrderRepository orderRepository,
        OutboxEventRepository outboxEventRepository
    ) {
        this.orderRepository = orderRepository;
        this.outboxEventRepository = outboxEventRepository;
    }

    @Override
    @Transactional
    public void save(OrderRequest request) throws IOException {
        Order order = new Order();
        order.setCustomerId(request.customerId());
        order.setTotal(request.total());

        orderRepository.save(order);

        outboxEventRepository.save(
            new OutboxEvent(
                EXCHANGE,
                "order.created",
                getJson(order),
                OutboxEvent.Status.PENDING
            )
        );
    }

    @Override
    @Transactional
    public OrderResponse update(long id, OrderRequest request) throws IOException {
        Order existingOrder = orderRepository.findById(id)
            .orElseThrow();

        existingOrder.setCustomerId(request.customerId());
        existingOrder.setTotal(request.total());

        outboxEventRepository.save(
            new OutboxEvent(
                EXCHANGE,
                "order.updated",
                getJson(existingOrder),
                OutboxEvent.Status.PENDING
            )
        );

        return new OrderResponse(existingOrder.getCustomerId(), existingOrder.getTotal());
    }

    private static String getJson(Order order) throws JsonProcessingException {
        String json = new ObjectMapper().writeValueAsString(
            new OrderCreatedEvent(order.getCustomerId(), order.getTotal())
        );

        log.info("Message: {}", json);

        return json;
    }
}
