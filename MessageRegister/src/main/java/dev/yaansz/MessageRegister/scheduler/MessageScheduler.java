package dev.yaansz.MessageRegister.scheduler;

import dev.yaansz.MessageRegister.config.RabbitMQConfig;
import dev.yaansz.MessageRegister.message.Message;
import dev.yaansz.MessageRegister.message.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class MessageScheduler {

    private static final Logger logger = LoggerFactory.getLogger(MessageScheduler.class);

    private final MessageRepository messageRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${scheduler.batch-size:100}")
    private int batchSize;

    @Value("${scheduler.max-retry-attempts:3}")
    private int maxRetryAttempts;

    @Value("${scheduler.retry-delay-minutes:5}")
    private int retryDelayMinutes;

    public MessageScheduler(MessageRepository messageRepository, RabbitTemplate rabbitTemplate) {
        this.messageRepository = messageRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelayString = "${scheduler.fixed-delay:10000}", initialDelayString = "${scheduler.initial-delay:5000}")
    @Transactional
    public void processScheduledMessages() {
        Instant now = Instant.now();
        List<Message> messages = messageRepository.findMessagesToProcess(now, PageRequest.of(0, batchSize));

        if(messages.isEmpty()) {
            return;
        }

        logger.info("Found {} messages to process", messages.size());

        for (Message message : messages) {
            try {
                processMessage(message);
            } catch (Exception e) {
                logger.error("Error processing message {}: {}", message.getId(), e.getMessage(), e);
                handleFailure(message);
            }
        }
    }

    private void processMessage(Message message) {
        logger.info("Processing message {} to {}", message.getId(), message.getEmail());

        // Send it to RabbitMQ
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                message
        );

        // Mark as queued
        message.markAsQueued();
        messageRepository.save(message);

        logger.info("Successfully queued message {} to RabbitMQ", message.getId());
    }

    private void handleFailure(Message message) {
        if (message.getRetryCount() < maxRetryAttempts) {
            // Schedule retry
            Instant nextRetry = Instant.now().plus(retryDelayMinutes, ChronoUnit.MINUTES);
            message.scheduleRetry(nextRetry);
            logger.warn("Scheduling retry {} for message {} at {}",
                    message.getRetryCount(), message.getId(), nextRetry);
        } else {
            // Max retries exceeded - failed to queue
            message.markAsFailedToQueue();
            logger.error("Message {} failed to queue after {} attempts", message.getId(), maxRetryAttempts);
        }
        messageRepository.save(message);
    }
}
