package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Query("SELECT url FROM Url WHERE hash = :hash")
    Optional<String> findUrlByHash(String hash);

    @Modifying
    @Query(value = """
            WITH deleted_rows AS (
                DELETE FROM url
                WHERE created_at < :dateTime
                RETURNING hash
            )
            SELECT hash FROM deleted_rows LIMIT :limit
            """, nativeQuery = true)
    List<String> deleteOutdatedUrls(LocalDateTime dateTime, int limit);
}