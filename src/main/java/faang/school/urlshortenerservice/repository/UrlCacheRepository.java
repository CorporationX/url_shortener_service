package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlCacheRepository extends JpaRepository<Url, String> {

    @Cacheable(cacheNames = "urls", key = "#id")
    Optional<Url> findById(String id);
}
