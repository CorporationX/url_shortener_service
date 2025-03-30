package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.UrlMapping;
import faang.school.urlshortenerservice.exception.NoSuchShortUrlException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<UrlMapping, String> {
    List<UrlMapping> findByExpiredAtBefore(LocalDateTime date);

    Optional<UrlMapping> findByHash(String hash);

    default UrlMapping findByHashThrow(String hash) {
        return findByHash(hash)
                .orElseThrow(() -> new NoSuchShortUrlException("Hash not found"));
    }
}
