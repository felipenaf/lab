package com.example.orderApp.service;

import com.example.orderApp.entity.OutboxEvent;
import com.example.orderApp.entity.Order;
import com.example.orderApp.event.OrderCreatedEvent;
import com.example.orderApp.repository.OrderRepository;
import com.example.orderApp.repository.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
public class OrderService {
    private static final String EXCHANGE = "order.exchange";
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;

    public OrderService(
        OrderRepository orderRepository,
        OutboxEventRepository outboxEventRepository
    ) {
        this.orderRepository = orderRepository;
        this.outboxEventRepository = outboxEventRepository;
    }

    @Transactional
    public void save(Order order) throws IOException {
        orderRepository.save(order);
        outboxEventRepository.save(
            new OutboxEvent(
                EXCHANGE,
                "order.created",
                getJson(order),
                OutboxEvent.Status.PENDING)
        );
    }

    @Transactional
    public void update(long id, Order order) throws IOException {
        Order existingOrder = orderRepository.findById(id)
            .orElseThrow();

        existingOrder.setCustomerId(order.getCustomerId());
        existingOrder.setTotal(order.getTotal());

        outboxEventRepository.save(
            new OutboxEvent(
                EXCHANGE,
                "order.updated",
                getJson(existingOrder),
                OutboxEvent.Status.PENDING
            )
        );
    }

    private static String getJson(Order order) throws JsonProcessingException {
        String json = new ObjectMapper().writeValueAsString(
            new OrderCreatedEvent(order.getCustomerId(), order.getTotal())
        );

        log.info("Message: {}", json);

        return json;
    }
}
