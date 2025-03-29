package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByHash(String hash);

    @Query(value = "DELETE FROM url WHERE created_at < NOW() - INTERVAL :days RETURNING hash",
            nativeQuery = true)
    List<String> deleteOldUrlsAndReturnHashes(int days);
}
