package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
@Log4j2
public class UrlService {
    private final HashCache hashCache;

    private final UrlRepository urlRepository;

    @CachePut(value = "url", key = "#result")
    @Transactional
    public String createShortUrlAndSave(@NotNull String url) {
        String hash = hashCache.getHash();
        if (hash == null) {
            log.error("Cannot get a hash for url: {}", url);
            throw new IllegalStateException("Failed to generate a hash for the URL.");
        }
        urlRepository.save(new Url(hash, url));
        return hash;
    }

    @Cacheable(value = "url", key = "#hash")
    public String getUrlByHash(String hash) {
        return urlRepository.findByHash(hash)
                .orElseThrow(() -> new EntityNotFoundException("URL not found for hash: " + hash)).getUrl();
    }
}