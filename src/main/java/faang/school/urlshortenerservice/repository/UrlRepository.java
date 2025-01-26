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
        DELETE FROM url WHERE hash IN (
            SELECT e.hash FROM url e WHERE e.created_at <= :dateTime
        ) RETURNING hash
        """)
    List<String> deleteOldUrlsAndReturnFreeHashes(LocalDateTime dateTime);
}
