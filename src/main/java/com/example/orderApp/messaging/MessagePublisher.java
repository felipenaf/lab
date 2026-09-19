package com.example.orderApp.messaging;

import java.io.IOException;

public interface MessagePublisher {
    void publish(String exchange, String routingKey, byte[] message) throws IOException;
}
