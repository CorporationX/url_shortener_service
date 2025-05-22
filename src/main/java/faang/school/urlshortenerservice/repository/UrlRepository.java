package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.enity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {

    @Modifying
    @Query(nativeQuery = true, value = """
            WITH to_delete AS (
                SELECT hash FROM url
                WHERE last_get_at <= :date
                ORDER BY last_get_at
                LIMIT :range
                FOR UPDATE SKIP LOCKED
            )
            DELETE FROM url
            USING to_delete
            WHERE url.hash = to_delete.hash
            RETURNING url.*;
            """)
    List<Url> deleteAndGetUnusedUrl(@Param(value = "date") LocalDateTime date, @Param("range") int range);

    Optional<Url> findByHash(String hash);
}
