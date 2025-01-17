package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UrlRepository extends JpaRepository<Url, String> {

    @Query(value = """
            SELECT url FROM url
            WHERE hash = :hash
            """, nativeQuery = true)
    Optional<String> findOriginalUrlByHash(String hash);

    @Query(value = """
            SELECT * FROM URL 
            WHERE hash in :urlHashes
            """, nativeQuery = true)
    List<Url> findByHashes(Set<String> urlHashes);

    @Query(value = """
        DELETE FROM url 
        WHERE created_at <= :thresholdDate 
        RETURNING hash
        """, nativeQuery = true)
    List<String> deleteOldUrls(@Param("thresholdDate") LocalDateTime thresholdDate);
}
