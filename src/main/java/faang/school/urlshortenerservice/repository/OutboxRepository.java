package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Outbox;
import faang.school.urlshortenerservice.entity.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox, Long> {

    @Query(nativeQuery = true, value = """
            UPDATE outbox o
            SET status = :#{#newStatus.ordinal}
            WHERE o.event_type = :eventType AND o.status = :#{#status.ordinal}
            RETURNING o.*
            """)
    List<Outbox> updateStatusByTypeAndByStatus(int eventType, OutboxStatus status, OutboxStatus newStatus);

    @Modifying
    @Query(nativeQuery = true, value = """
            INSERT INTO outbox (entity_id, event_type, payload)
            VALUES (:entityId, :eventType, :payload)
            """)
    void createOutbox(String entityId, int eventType, String payload);

    @Modifying
    @Query("""
            UPDATE Outbox o
            SET o.status = :status, o.processedAt = CURRENT_TIMESTAMP
            WHERE o.id = :id
            """)
    void progressToEnd(Long id, OutboxStatus status);
}
