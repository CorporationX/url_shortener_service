package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    @Query(nativeQuery = true, value = """
            DELETE FROM url WHERE created_at < NOW() - INTERVAL '1 year'
            RETURNING hash
            """)
    List<String> getOldHash();

    @Query(nativeQuery = true, value = """
            SELECT url FROM url WHERE hash = :hash
            """)
    Optional<String> getUrl(String hash);
}
