package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, String> {
    Optional<Url> findByHash(String hash);

    @Query(nativeQuery = true, value = """
            DELETE FROM url
            WHERE hash IN (
                SELECT hash FROM url
                WHERE expire_at < NOW()
                FOR UPDATE SKIP LOCKED
                );
            """)
    void deleteExpiredUrls();
}
