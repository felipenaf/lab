package com.example.orderApp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;

    private String key;

    private String payload;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {PENDING, PROCESSED}

    public OutboxEvent() {}

    public OutboxEvent(String eventType, String key, String payload, Status status) {
        this.eventType = eventType;
        this.key = key;
        this.payload = payload;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
