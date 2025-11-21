package dev.yaansz.MessageRegister.dto;

import java.time.Instant;
import java.util.UUID;

public class MessageSuccessDTO {
    private UUID messageId;
    private Instant sentAt;
    private String status;

    public MessageSuccessDTO() {
    }

    public MessageSuccessDTO(UUID messageId, Instant sentAt, String status) {
        this.messageId = messageId;
        this.sentAt = sentAt;
        this.status = status;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
