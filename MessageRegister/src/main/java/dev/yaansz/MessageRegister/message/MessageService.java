package dev.yaansz.MessageRegister.message;

import dev.yaansz.MessageRegister.config.UnleashService;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);
    private final MessageRepository repository;
    private final UnleashService unleashService;

    public MessageService(MessageRepository repository, UnleashService unleashService) {
        this.repository = repository;
        this.unleashService = unleashService;
    }

    public Message save(@NonNull Message message) {
        log.info("Attempting to save message for user: {}, email: {}", message.getUserId(), message.getEmail());
        
        boolean isEmailSendEnabled = unleashService.isEmailSendEnabled(message.getUserId(), message.getEmail());
        
        if (!isEmailSendEnabled) {
            log.info("Email send feature is disabled for user: {}, email: {}", message.getUserId(), message.getEmail());
            message.setStatus(MessageStatus.BLOCKED);
        }
        

        log.debug("Message details - userId: {}, email: {}, scheduledTime: {}, status: {}",
                message.getUserId(), message.getEmail(), message.getScheduledTime(), message.getStatus());
        
        Message savedMessage = repository.save(message);
        log.info("Message saved successfully with id: {}, status: {}", savedMessage.getId(), savedMessage.getStatus());
        
        return savedMessage;
    }

    public Optional<Message> findById(@NonNull UUID id) {
        log.info("Finding message by id: {}", id);
        Optional<Message> message = repository.findById(id);
        
        if (unleashService.isDebugLoggingEnabled("MessageService") || unleashService.isDebugLoggingEnabledGlobally()) {
            log.debug("Message found: {}", message.isPresent());
        }
        
        return message;
    }

    public Page<@NonNull Message> findAll(Pageable pageable) {
        log.info("Finding all messages with page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<@NonNull Message> messages = repository.findAll(pageable);
        
        if (unleashService.isDebugLoggingEnabled("MessageService") || unleashService.isDebugLoggingEnabledGlobally()) {
            log.debug("Found {} messages", messages.getTotalElements());
        }
        
        return messages;
    }
}
