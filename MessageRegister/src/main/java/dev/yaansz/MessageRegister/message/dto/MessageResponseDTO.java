package dev.yaansz.MessageRegister.message.dto;

import dev.yaansz.MessageRegister.message.Message;
import dev.yaansz.MessageRegister.message.MessageStatus;

import java.time.Instant;
import java.util.UUID;

public record MessageResponseDTO(
        UUID id,
        String body,
        String email,
        UUID userId,
        Instant scheduledTime,
        MessageStatus status,
        Integer retryCount,
        Instant nextRetryTime,
        Instant queuedAt,
        Instant createdAt,
        Instant updatedAt
) {
    public static MessageResponseDTO fromEntity(Message message) {
        return new MessageResponseDTO(
                message.getId(),
                message.getBody(),
                message.getEmail(),
                message.getUserId(),
                message.getScheduledTime(),
                message.getStatus(),
                message.getRetryCount(),
                message.getNextRetryTime(),
                message.getQueuedAt(),
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }
}
