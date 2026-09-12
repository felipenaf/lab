package com.example.consumer.messaging;

import java.io.IOException;
import java.util.function.Consumer;

public interface MessageConsumer {
    void consume(String queue, String exchange, String routingKey, Consumer<Message> handler) throws IOException;
}
