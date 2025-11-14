package dev.yaansz.MessageRegister.message;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "email_message")
@EntityListeners(AuditingEntityListener.class)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String body;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Instant scheduledTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    @Column(nullable = false)
    private Integer retryCount = 0;

    @Column
    private Instant nextRetryTime;

    @Column
    private Instant queuedAt;

    @Column(nullable = false)
    @CreatedDate
    private  Instant createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    private Instant updatedAt;

    public Message() {
    }

    public Message(String body, String email, Instant scheduledTime, MessageStatus status) {
        this.body = body;
        this.email = email;
        this.scheduledTime = scheduledTime;
        this.status = status;
        this.retryCount = 0;
    }

    public UUID getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public String getEmail() {
        return email;
    }

    public Instant getScheduledTime() {
        return scheduledTime;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Instant getNextRetryTime() {
        return nextRetryTime;
    }

    public void setNextRetryTime(Instant nextRetryTime) {
        this.nextRetryTime = nextRetryTime;
    }

    public Instant getQueuedAt() {
        return queuedAt;
    }

    public void setQueuedAt(Instant queuedAt) {
        this.queuedAt = queuedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }

    public void markAsQueued() {
        this.status = MessageStatus.QUEUED;
        this.queuedAt = Instant.now();
    }

    public void markAsFailedToQueue() {
        this.status = MessageStatus.FAILURE;
    }

    public void scheduleRetry(Instant retryTime) {
        this.nextRetryTime = retryTime;
        this.incrementRetryCount();
    }
}
