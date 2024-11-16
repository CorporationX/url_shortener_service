package faang.school.urlshortenerservice.repository.url;

import faang.school.urlshortenerservice.entity.Url;
import jakarta.transaction.Transactional;
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
    @Transactional
    @Query(nativeQuery = true, value = """
        DELETE FROM urls
        WHERE created_at < :cutoffDate
        RETURNING hash
        """)
    List<String> deleteExpiredUrlsAndReturnHashes(@Param("cutoffDate") LocalDateTime cutoffDate);
}
