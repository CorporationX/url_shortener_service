package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByHash(String hash);

    @Query(nativeQuery = true, value = """
            DELETE FROM url_shortener_schema.url
            WHERE created_at < (CURRENT_DATE - INTERVAL '1 year')
            RETURNING hash
            LIMIT :limit
            """)
    List<String> deleteAndGetHashes(int limit);
}
