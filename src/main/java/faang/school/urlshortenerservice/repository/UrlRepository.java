package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, String> {

    Optional<Url> findByUrl(String url);

    Optional<Url> findByHash(String hash);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM url u
            WHERE hash IN
            (
                SELECT u.hash FROM url u
                WHERE u.created_at < :time
            )
            RETURNING u.url, u.hash, u.created_at
            """)
    List<Url> findOlderUrls(LocalDateTime time);
}
