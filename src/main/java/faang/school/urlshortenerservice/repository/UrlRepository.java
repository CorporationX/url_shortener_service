package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    Optional<Url> findByHash(String hash);

    Optional<Url> findByUrl(String url);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM url
            WHERE created_at < :date
            RETURNING hash
            """)
    List<String> cleanExpiredUrls(@Param("date") Instant expiryDate);
}
