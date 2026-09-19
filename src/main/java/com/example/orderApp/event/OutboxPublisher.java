package com.example.orderApp.event;

import com.example.orderApp.entity.OutboxEvent;
import com.example.orderApp.messaging.MessagePublisher;
import com.example.orderApp.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class OutboxPublisher {
    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);
    private static final int delay = 10000;
    private static final int pagination = 1000;
    private final OutboxEventRepository outboxEventRepository;
    private final MessagePublisher messagePublisher;

    public OutboxPublisher(
        OutboxEventRepository outboxEventRepository,
        MessagePublisher messagePublisher
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.messagePublisher = messagePublisher;
    }

    @Scheduled(fixedDelay = delay)
    public void publishPendingEvents() {
        List<OutboxEvent> events = outboxEventRepository.findByStatus(
            OutboxEvent.Status.PENDING,
            PageRequest.of(0, pagination)
        );

        log.info("Pending events {}", events.size());

        for (OutboxEvent event : events) {
            try {
                messagePublisher.publish(
                    event.getEventType(),
                    event.getKey(),
                    event.getPayload().getBytes(StandardCharsets.UTF_8)
                );

                event.setStatus(OutboxEvent.Status.PROCESSED);
                outboxEventRepository.save(event);
            } catch (Exception e) {
                log.error("Failed to publish event {}", event.getId(), e);
            }
        }
    }
}