package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;


public interface UrlRepository extends JpaRepository<Url, String> {
    @Query(nativeQuery = true, value = """
        DELETE FROM url u
        WHERE u.hash IN (
            SELECT hash
            FROM url
            WHERE created_at < :before
        )
        RETURNING hash
        """)
    List<String> deleteOldUrlsAndGetReleasedHashes(@Param("before") LocalDateTime createdBefore);
}
