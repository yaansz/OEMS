package dev.yaansz.MessageRegister.message;

import jakarta.persistence.LockModeType;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<@NonNull Message, @NonNull UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT m FROM Message m
            WHERE m.scheduledTime <= :now
            AND m.status = dev.yaansz.MessageRegister.message.MessageStatus.PENDING
            AND (m.nextRetryTime IS NULL OR m.nextRetryTime <= :now)
            ORDER BY m.scheduledTime ASC
            """)
    List<Message> findMessagesToProcess(@Param("now") Instant now, Pageable pageable);
}
