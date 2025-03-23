package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    List<Url> findByCreatedAtBefore(LocalDateTime deleteDate, Pageable pageable);
    Optional<Url> findUrlByUrl(String url);
    Optional<Url> findUrlByHash(String hash);
    int countByCreatedAtBefore(LocalDateTime deleteDate);
}
