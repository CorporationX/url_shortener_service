package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Query(nativeQuery = true, value = """
            DELETE FROM url
            WHERE created_at < NOW() - INTERVAL '1 year'
            RETURNING hash
            """)
    @Modifying
    List<String> retrieveOldHashes();

    Optional<Url> findByHash(String hash);

    @Query(nativeQuery = true, value = """
            SELECT hash FROM url
            WHERE url = ?1
            """)
    Optional<String> findByUrl(String url);
}
