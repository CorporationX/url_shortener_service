package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<ShortUrl, String> {

    @Modifying
    @Query(nativeQuery = true, value = """
                    DELETE FROM short_url su
                    WHERE su.created_at < :dateThreshold
                    RETURNING su.hash
            """)
    List<String> getExpiredHashes(@Param("dateThreshold") LocalDateTime dateThreshold);
}
