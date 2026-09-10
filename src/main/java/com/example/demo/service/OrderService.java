package com.example.demo.service;

import com.example.demo.entity.Order;
import com.example.demo.event.OrderCreatedEvent;
import com.example.demo.properties.RabbitmqProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Service
public class OrderService {
    private static final int PERSISTENT = 2;
    private static final String EXCHANGE = "order.exchange";
    private final AMQP.BasicProperties props;
    private final ConnectionFactory connectionFactory;

    public OrderService(RabbitmqProperties env) {
        this.props = new AMQP.BasicProperties.Builder()
            .contentType("application/json")
            .deliveryMode(PERSISTENT)
            .build();

        this.connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(env.getHost());
        connectionFactory.setPort(env.getPort());
        connectionFactory.setUsername(env.getUsername());
        connectionFactory.setPassword(env.getPassword());
    }

    public void save(@RequestBody Order order)
        throws IOException, TimeoutException
    {
        Channel channel = connectionFactory.newConnection().createChannel();
        channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.TOPIC, true);

        String json = getJson(order);

        channel.basicPublish(
            EXCHANGE,
            "order.created",
            props,
            json.getBytes(StandardCharsets.UTF_8)
        );

        System.out.println("-- Order Producer - Message published --");
        System.out.println(json);
        System.out.println("----------------------------------------");
    }

    private static String getJson(Order order) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(
            new OrderCreatedEvent(order.getCustomerId(), order.getTotal())
        );
    }
}
