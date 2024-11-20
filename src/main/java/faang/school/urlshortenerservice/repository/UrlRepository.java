package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    Optional<Url> getUrlByHash(String hash);

    @Query(value = "SELECT * FROM url WHERE created_at < NOW() - INTERVAL '1 year' ORDER BY created_at LIMIT 100", nativeQuery = true)
    List<Url> findOldUrls();
}