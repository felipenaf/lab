package com.example.orderApp.configuration.rabbitMQ;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RabbitMQStructure {
    private static final Logger log = LoggerFactory.getLogger(RabbitMQStructure.class);
    private final Channel channel;
    private final RabbitmqProperties properties;

    public RabbitMQStructure(Channel channel, RabbitmqProperties properties) {
        this.channel = channel;
        this.properties = properties;
    }

    @PostConstruct
    public void create() {
        properties.queues().forEach(data -> {
            try {
                log.info("data {}", data);

                channel.exchangeDeclare(
                    data.exchangeName(),
                    BuiltinExchangeType.valueOf(data.exchangeType().toUpperCase()),
                    true
                );

                channel.queueDeclare(data.queue(), true, false, false, null);
                channel.queueBind(data.queue(), data.exchangeName(), data.routingKey());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
