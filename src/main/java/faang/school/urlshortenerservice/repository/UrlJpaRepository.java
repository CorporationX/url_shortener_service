package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UrlJpaRepository extends JpaRepository<Url, String> {

    Optional<Url> findByUrl(String url);

    Optional<Url> findByHash(String hash);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM url
            WHERE last_received_at < :timestamp
            RETURNING url, hash, created_at, last_received_at
            """)
    List<Url> findUrlsOlderThan(LocalDateTime timestamp);
}