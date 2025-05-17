package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.enity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM free_hash
            WHERE last_get_at <= :date
            """)
    void deleteUnusedUrl(@Param(value = "date") LocalDateTime date);

    Optional<Url> findByUrl(String url);

    Optional<Url> findByHash(String hash);
}
