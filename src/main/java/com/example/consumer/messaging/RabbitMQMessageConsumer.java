package com.example.consumer.messaging;

import com.rabbitmq.client.Channel;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.function.Consumer;

@Component
public class RabbitMQMessageConsumer implements MessageConsumer {
    private final Channel channel;

    public RabbitMQMessageConsumer(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void consume(
        String queue, String exchange, String routingKey, Consumer<Message> handler
    ) throws IOException {
        // Guarantee the creation of exchange and queue if it doesn't exist
        // channel.exchangeDeclare("order.exchange", BuiltinExchangeType.DIRECT, true);
        channel.queueDeclare(queue, true, false, false, null);

        // Guarantee the association of the queue with the exchange if it doesn't exist
        channel.queueBind(queue, exchange, routingKey);

        channel.basicConsume(
            queue,
            false,
            (consumerTag, delivery) -> {
                Message message = new Message(
                    delivery.getEnvelope().getExchange(),
                    delivery.getEnvelope().getRoutingKey(),
                    delivery.getBody()
                );

                System.out.println("-- " + this.getClass().getSimpleName() + " --");
                System.out.println(
                    "Deliver Tag: " + delivery.getEnvelope().getDeliveryTag() + " | Consumer Tag: " + consumerTag
                );
                System.out.println("--------------------------");

                try {
                    handler.accept(message);
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                } catch (Exception e) {
                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
                }
            },
            consumerTag -> {}
        );

        System.out.println(this.getClass().getSimpleName() + " - Waiting for messages...");
    }
}
