package dev.yaansz.MessageSender.consumer;

import dev.yaansz.MessageSender.config.RabbitMQConfig;
import dev.yaansz.MessageSender.dto.MessageDTO;
import dev.yaansz.MessageSender.dto.MessageSuccessDTO;
import dev.yaansz.MessageSender.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class MessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    private final EmailService emailService;
    private final RabbitTemplate rabbitTemplate;

    public MessageConsumer(EmailService emailService, RabbitTemplate rabbitTemplate) {
        this.emailService = emailService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.MESSAGE_QUEUE)
    public void consumeMessage(MessageDTO message) {
        logger.info("Received message {} for email {}", message.getId(), message.getEmail());

        try {
            // Send email via SMTP
            emailService.sendEmail(message);

            // Send success response back to MessageRegister
            MessageSuccessDTO successResponse = new MessageSuccessDTO(
                    message.getId(),
                    Instant.now(),
                    "SUCCESS"
            );

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.SUCCESS_EXCHANGE,
                    RabbitMQConfig.SUCCESS_ROUTING_KEY,
                    successResponse
            );

            logger.info("Successfully processed message {} and sent success response", message.getId());
        } catch (Exception e) {
            logger.error("Failed to process message {}: {}", message.getId(), e.getMessage(), e);
            // In a production scenario, you might want to send a failure response or use DLQ
            throw e; // This will trigger RabbitMQ's retry mechanism
        }
    }
}
