package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    @Modifying
    @Query(value = """
               WITH to_delete AS (
                    SELECT url.hash FROM url
                    WHERE url.createdAt < :cutoff
                    LIMIT :limit
                    FOR UPDATE SKIP LOCKED
                )
                DELETE FROM url
                USING to_delete
                WHERE url.hash = to_delete.hash
                RETURNING url.hash
            """, nativeQuery = true)
    List<String> deleteOldReturningHashes(@Param("cutoff") LocalDateTime cutoff, @Param("limit") int limit);
}
