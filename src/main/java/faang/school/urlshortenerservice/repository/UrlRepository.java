package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<ShortUrl, String> {

    Optional<ShortUrl> findByHash(String hash);

    @Query(nativeQuery = true, value = """
            SELECT hash FROM short_url
            WHERE expiration_time < NOW()
            FOR UPDATE SKIP LOCKED
            LIMIT :limit""")
    List<String> findExpiredUrlsHashes(@Param("limit") int limit);
}