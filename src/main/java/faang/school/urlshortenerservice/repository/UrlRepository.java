package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    Optional<String> findUrlByHash(String hash);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM url
            WHERE created_at < NOW() - INTERVAL ':interval'
            RETURNING hash
            """)
    List<String> deleteOldUrlsAndReturnHashes(String interval);
}
