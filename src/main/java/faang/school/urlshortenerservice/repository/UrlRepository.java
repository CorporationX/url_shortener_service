package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface UrlRepository extends JpaRepository<Url, String> {
    @Query(nativeQuery = true,
            value = """
                    DELETE FROM url
                    WHERE url.created_at < :before
                    RETURNING hash
                    """)
    List<Hash> deleteOldUrlsAndGetReleasedHashes(@Param("before") LocalDateTime createdBefore);

}
