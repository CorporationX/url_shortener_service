package faang.school.urlshortenerservice.repository.jpa;

import faang.school.urlshortenerservice.model.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findByHash(String hash);

    @Query(nativeQuery = true, value = """
                DELETE FROM url
                WHERE id IN (
                    SELECT id
                    FROM url
                    WHERE expired_at IS NOT NULL
                    AND expired_at <= CURRENT_TIMESTAMP
                    LIMIT :limit
                    FOR UPDATE SKIP LOCKED)
                 RETURNING *;
            """)
    @Modifying
    List<Url> deleteExpiredUrls(int limit);
}
