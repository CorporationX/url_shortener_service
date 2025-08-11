package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<UrlEntity, String> {
    Optional<UrlEntity> findByOriginalUrl(String originalUrl);
}
