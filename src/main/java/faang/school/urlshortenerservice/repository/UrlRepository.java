package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    Optional<Url> findByHash(String hash);

    @Modifying
    @Query(value = """
                DELETE FROM url
                WHERE expires_at < :expirationDate
                RETURNING hash
            """, nativeQuery = true)
    List<String> deleteOldUrlsAndReturnHashes(@Param("expirationDate") LocalDateTime expirationDate);

    @Query(value = """
        SELECT * FROM url
        WHERE expires_at > now()
        ORDER BY created_at DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Url> findTopUrls(@Param("limit") int limit);
}