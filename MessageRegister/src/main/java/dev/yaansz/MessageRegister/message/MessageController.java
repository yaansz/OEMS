package dev.yaansz.MessageRegister.message;

import dev.yaansz.MessageRegister.config.UnleashService;
import dev.yaansz.MessageRegister.message.dto.MessageCreateDTO;
import dev.yaansz.MessageRegister.message.dto.MessageResponseDTO;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/messages")
public class MessageController {

    private static final Logger log = LoggerFactory.getLogger(MessageController.class);
    private final MessageService messageService;
    private final UnleashService unleashService;

    public MessageController(MessageService messageService, UnleashService unleashService) {
        this.messageService = messageService;
        this.unleashService = unleashService;
    }

    @PostMapping
    public ResponseEntity<@NonNull MessageResponseDTO> createMessage(@Valid @RequestBody MessageCreateDTO messageCreateDTO) {
        log.info("Received request to create message for email: {}", messageCreateDTO.email());

        log.debug("Message creation request details - userId: {}, email: {}, scheduledTime: {}",
                messageCreateDTO.userId(), messageCreateDTO.email(), messageCreateDTO.scheduledTime());
        
        Message message = new Message(
                messageCreateDTO.body(),
                messageCreateDTO.email(),
                messageCreateDTO.userId(),
                messageCreateDTO.scheduledTime(),
                MessageStatus.PENDING
        );
        Message savedMessage = messageService.save(message);
        
        log.info("Message created successfully with id: {}", savedMessage.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(MessageResponseDTO.fromEntity(savedMessage));
    }

    @GetMapping("/{id}")
    public ResponseEntity<@NonNull MessageResponseDTO> getMessage(@PathVariable UUID id) {
        log.info("Received request to get message with id: {}", id);
        
        return messageService.findById(id)
                .map(message -> {
                    log.info("Message found with id: {}", id);
                    return ResponseEntity.ok(MessageResponseDTO.fromEntity(message));
                })
                .orElseGet(() -> {
                    log.info("Message not found with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping
    public ResponseEntity<@NonNull Page<@NonNull MessageResponseDTO>> getAllMessages(@PageableDefault(size = 20) Pageable pageable) {
        log.info("Received request to get all messages");
        
        if (unleashService.isDebugLoggingEnabled("MessageController") || unleashService.isDebugLoggingEnabledGlobally()) {
            log.debug("Pagination details - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        }
        
        Page<@NonNull MessageResponseDTO> messages = messageService.findAll(pageable)
                .map(MessageResponseDTO::fromEntity);
        
        log.info("Returning {} messages", messages.getNumberOfElements());
        
        return ResponseEntity.ok(messages);
    }
}
