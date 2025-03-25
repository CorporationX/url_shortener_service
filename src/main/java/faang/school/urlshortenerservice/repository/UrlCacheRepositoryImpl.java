package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UrlCacheRepositoryImpl implements UrlCacheRepository {
    private static final String CACHE = "cache-url";
    private final UrlRepository urlRepository;

    @CachePut(CACHE)
    @Override
    public Url save(Url url) {
        log.info("Saving url {}", url);
        return urlRepository.save(url);
    }

    @Cacheable(CACHE)
    @Override
    public Url findByHash(String hash) {
        log.info("Find url by hash {}", hash);
        return urlRepository.findByHash(hash).orElseThrow(() -> new EntityNotFoundException("Url Not Found"));
    }

    @CacheEvict(CACHE)
    @Override
    public List<Url> deleteAndReturnByCreatedAtBefore(LocalDateTime createdAt) {
        log.info("Deleting urls before {}", createdAt);
        return urlRepository.deleteAndReturnByCreatedAtBefore(createdAt);
    }
}
