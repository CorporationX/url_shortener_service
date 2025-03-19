package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UrlRepository extends CrudRepository<Url, Long> {
    Optional<Url> findByHash(String hash);
    Page<Url> findByCreatedAtBefore(LocalDateTime dateTime, Pageable pageable);
}