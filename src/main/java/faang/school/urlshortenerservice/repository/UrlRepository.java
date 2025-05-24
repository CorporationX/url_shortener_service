package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, String> {

    Optional<Url> findByHash(String hash);

    @Query("SELECT u.hash FROM Url u WHERE u.url = :url")
    String findHashByUrl(String url);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM url
            WHERE created_at < CURRENT_TIMESTAMP - INTERVAL '1 year'
            RETURNING hash
            """)
    List<String> deleteExpiredUrls();
}