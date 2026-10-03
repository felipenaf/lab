package com.example.orderApp.configuration.rabbitMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    private final RabbitmqProperties properties;

    public RabbitMQConfig(RabbitmqProperties properties) {
        this.properties = properties;
    }

    @Bean
    public Connection rabbitConnection() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(properties.host());
        factory.setPort(properties.port());
        factory.setUsername(properties.username());
        factory.setPassword(properties.password());

        return factory.newConnection();
    }

    @Bean
    public Channel rabbitChannel(Connection connection) throws Exception {
        return connection.createChannel();
    }
}
