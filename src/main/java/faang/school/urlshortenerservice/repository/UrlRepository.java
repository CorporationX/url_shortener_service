package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    boolean existsByUrl(String url);

    Url findByUrl(String url);

    @Query(value = """
                DELETE FROM url
                WHERE created_at < :threshold
                RETURNING hash
            """, nativeQuery = true)
    List<String> deleteOldUrlsAndReturnHashes(LocalDateTime threshold);

}
