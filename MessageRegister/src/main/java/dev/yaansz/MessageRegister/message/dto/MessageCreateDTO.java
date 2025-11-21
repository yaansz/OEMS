package dev.yaansz.MessageRegister.message.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.util.UUID;

public record MessageCreateDTO(
        @NotNull @Size(min = 1, max = 1000) String body,
        @NotNull @Email String email,
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant scheduledTime,
        @NotNull UUID userId
) {
}
