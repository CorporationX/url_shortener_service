package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    List<Url> findByCreatedAtBefore(LocalDateTime deleteDate, Pageable pageable);
    int countByCreatedAtBefore(LocalDateTime deleteDate);
}
