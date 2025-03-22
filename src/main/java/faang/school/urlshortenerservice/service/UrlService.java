package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@CacheConfig(cacheNames = "urls")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;

    @Cacheable
    public String getOriginalUrl(String hash) {
        Url url = urlRepository.findByHash(hash)
                .orElseThrow(() -> new EntityNotFoundException("Url not found"));
        return url.getUrl();
    }
}