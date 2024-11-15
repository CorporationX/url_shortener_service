package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox, Long> {

    @Query("""
            SELECT o FROM Outbox o
            WHERE o.eventType = :eventType
            """)
    List<Outbox> findByEventType(int eventType);

    @Modifying
    @Query(nativeQuery = true, value = """
            INSERT INTO outbox (entity_id, event_type, payload)
            VALUES (:entityId, :eventType, :payload)
            """)
    void createOutbox(String entityId, int eventType, String payload);
}
