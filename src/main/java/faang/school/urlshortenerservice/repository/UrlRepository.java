package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    boolean existsByUrl(String url);

    @Query(nativeQuery = true, value = """
            WITH deleted AS (
                SELECT hash FROM url
                WHERE created_at > :dateTime
                LIMIT :count
            )
            DELETE FROM url USING deleted
            WHERE url.hash = deleted.hash
            RETURNING url.hash;
            """)
    List<String> deleteOutdatedUrlsAndGetHashes(LocalDateTime dateTime, int count);
}
