package dev.yaansz.MessageRegister.message;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class MessageService {

    private final MessageRepository repository;

    public MessageService(MessageRepository repository) {
        this.repository = repository;
    }

    public Message save(@NonNull Message message) {
        return repository.save(message);
    }

    public Optional<Message> findById(@NonNull UUID id) {
        return repository.findById(id);
    }

    public Page<@NonNull Message> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
