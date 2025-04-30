package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Query(nativeQuery = true, value = """
            SELECT * FROM url
            ORDER BY created_at DESC
            LIMIT :limit
            """)
    List<Url> findRecentUrls(int limit);

    @Query(nativeQuery = true, value = """
            DELETE FROM url
            WHERE created_at <= :date
            RETURNING hash
            """)
    List<String> deleteOldUrlsAndReturnHashes(LocalDateTime date);
}
