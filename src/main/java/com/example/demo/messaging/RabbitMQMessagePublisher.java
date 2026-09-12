package com.example.demo.messaging;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RabbitMQMessagePublisher implements MessagePublisher {
    private static final int PERSISTENT = 2;
    private final Channel channel;

    public RabbitMQMessagePublisher(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void publish(String exchange, String routingKey, byte[] message) throws IOException {
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
            .contentType("application/json")
            .deliveryMode(PERSISTENT)
            .build();

        channel.exchangeDeclare(exchange, BuiltinExchangeType.TOPIC, true);
        channel.basicPublish(exchange, routingKey, props, message);
    }
}
