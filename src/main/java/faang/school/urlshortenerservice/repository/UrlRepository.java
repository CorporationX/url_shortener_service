package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface UrlRepository extends JpaRepository<Url, String> {
    Slice<Url> findByCreatedAtBefore(LocalDateTime createdAtBefore, Pageable pageable);
}
