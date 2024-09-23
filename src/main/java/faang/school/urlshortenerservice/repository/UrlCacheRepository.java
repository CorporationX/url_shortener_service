package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final UrlRepository urlRepository;

    @CachePut(value = "originalUrl", key = "#hash")
    public Url save(String hash, String originalUrl) {
        return urlRepository.create(hash, originalUrl);
    }

    @Cacheable(value = "originalUrl", key = "#hash")
    public Url getUrl(String hash) {
        return urlRepository.findByHash(hash)
                .orElseThrow(() -> new NotFoundException("Url for hash " + hash + " is not found"));
    }

    //@Cacheable(value = "originalUrl", key = "#hash")
    public Optional<Url> getByOriginalUrl(String originalUrl) {
        return urlRepository.findByOriginalUrl(originalUrl);
    }
}