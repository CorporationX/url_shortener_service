package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    List<Url> findAllByCreatedAtBefore(LocalDateTime dateTime);

    Url findByHash(String hash);

    @Modifying
    @Transactional
    @Query(value = """
    WITH cte AS (
        SELECT hash
        FROM url
        WHERE created_at < :cutoffDate
        FOR UPDATE SKIP LOCKED
    )
    DELETE FROM url
    WHERE hash IN (SELECT hash FROM cte)
    RETURNING hash
    """, nativeQuery = true)
    List<String> deleteAllByCreatedAtBeforeReturningHashes(@Param("cutoffDate") LocalDateTime cutoffDate);
}