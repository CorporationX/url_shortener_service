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

    @Query(nativeQuery = true, value = """
            DELETE FROM url u
            WHERE hash IN
            (
                SELECT u.hash FROM url u
                WHERE u.last_received_at < :timestamp
            )
            RETURNING u.url, u.hash, u.created_at, u.last_received_at
            """)
    @Modifying
    List<Url> findUrlsOlderThan(LocalDateTime timestamp);
}
