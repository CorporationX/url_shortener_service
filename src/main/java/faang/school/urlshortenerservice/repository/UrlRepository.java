package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findUrlByHash(String hash);

    Optional<Url> findByUrl(String url);

    @Modifying
    @Query(nativeQuery = true, value = """
               DELETE FROM url WHERE url.created_at < now() - INTERVAL '1 year' RETURNING url.hash
            """)
    List<String> removeUrlOlderThanOneYear();
}
