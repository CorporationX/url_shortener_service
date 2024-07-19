package faang.school.urlshortenerservice.repository;


import faang.school.urlshortenerservice.model.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final JpaUrlCacheRepository jpaUrlCacheRepository;


    @Cacheable(cacheNames = "urls", key = "#id")
    public Optional<Url> findUrlById(String id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        return jpaUrlCacheRepository.findById(id);
    }
}
