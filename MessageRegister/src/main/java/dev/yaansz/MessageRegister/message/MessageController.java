package dev.yaansz.MessageRegister.message;

import dev.yaansz.MessageRegister.message.dto.MessageCreateDTO;
import dev.yaansz.MessageRegister.message.dto.MessageResponseDTO;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
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

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<@NonNull MessageResponseDTO> createMessage(@Valid @RequestBody MessageCreateDTO messageCreateDTO) {
        Message message = new Message(
                messageCreateDTO.body(),
                messageCreateDTO.email(),
                messageCreateDTO.scheduledTime(),
                MessageStatus.PENDING
        );
        Message savedMessage = messageService.save(message);
        return ResponseEntity.status(HttpStatus.CREATED).body(MessageResponseDTO.fromEntity(savedMessage));
    }

    @GetMapping("/{id}")
    public ResponseEntity<@NonNull MessageResponseDTO> getMessage(@PathVariable UUID id) {
        return messageService.findById(id)
                .map(message -> ResponseEntity.ok(MessageResponseDTO.fromEntity(message)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<@NonNull Page<@NonNull MessageResponseDTO>> getAllMessages(@PageableDefault(size = 20) Pageable pageable) {
        Page<@NonNull MessageResponseDTO> messages = messageService.findAll(pageable)
                .map(MessageResponseDTO::fromEntity);
        return ResponseEntity.ok(messages);
    }
}
