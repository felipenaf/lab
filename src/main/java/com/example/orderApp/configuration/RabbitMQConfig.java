package com.example.orderApp.configuration;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    private final RabbitmqProperties rabbitmqProperties;

    public RabbitMQConfig(RabbitmqProperties rabbitmqProperties) {
        this.rabbitmqProperties = rabbitmqProperties;
    }

    @Bean
    public Connection rabbitConnection() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitmqProperties.host());
        factory.setPort(rabbitmqProperties.port());
        factory.setUsername(rabbitmqProperties.username());
        factory.setPassword(rabbitmqProperties.password());

        return factory.newConnection();
    }

    @Bean
    public Channel rabbitChannel(Connection connection) throws Exception {
        return connection.createChannel();
    }
}
