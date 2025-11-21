package dev.yaansz.MessageRegister.consumer;

import dev.yaansz.MessageRegister.config.RabbitMQConfig;
import dev.yaansz.MessageRegister.dto.MessageSuccessDTO;
import dev.yaansz.MessageRegister.message.Message;
import dev.yaansz.MessageRegister.message.MessageRepository;
import dev.yaansz.MessageRegister.message.MessageStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MessageSuccessConsumer {

    private static final Logger logger = LoggerFactory.getLogger(MessageSuccessConsumer.class);

    private final MessageRepository messageRepository;

    public MessageSuccessConsumer(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.SUCCESS_QUEUE)
    public void consumeSuccessMessage(MessageSuccessDTO successDTO) {
        logger.info("Received success response for message {}", successDTO.getMessageId());

        try {
            Optional<Message> messageOpt = messageRepository.findById(successDTO.getMessageId());
            
            if (messageOpt.isPresent()) {
                Message message = messageOpt.get();
                message.setStatus(MessageStatus.SUCCESS);
                messageRepository.save(message);
                
                logger.info("Successfully updated message {} status to SUCCESS", successDTO.getMessageId());
            } else {
                logger.warn("Message {} not found in database", successDTO.getMessageId());
            }
        } catch (Exception e) {
            logger.error("Failed to process success response for message {}: {}", 
                successDTO.getMessageId(), e.getMessage(), e);
            throw e;
        }
    }
}
