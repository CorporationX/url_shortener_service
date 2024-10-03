package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlJpaRepository;
import faang.school.urlshortenerservice.validator.DurationValidator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlService {
    @Value("${url.cache.initial.ttl}")
    private Duration initialTimeout;

    @Value("${url.cache.secondary.ttl}")
    private Duration secondaryTimeout;

    private final UrlMapper urlMapper;
    private final UrlJpaRepository urlRepository;
    private final HashCache hashCache;
    private final DurationValidator durationValidator;
    private final UrlCacheRepository urlCacheRepository;
    private final Map<String, Object> locks = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        durationValidator.validateDurationIsNotNull(initialTimeout);
        durationValidator.validateDurationIsNotNull(secondaryTimeout);
    }

    @Transactional
    public UrlDto createShortUrl(UrlDto urlDto) {
        Url redirectUrl = urlMapper.toEntity(urlDto);
        String hash = hashCache.getHash();
        redirectUrl.setHash(hash);
        Url savedUrl = urlRepository.save(redirectUrl);
        urlCacheRepository.save(savedUrl.getHash(), savedUrl.getUrl(), initialTimeout);
        return urlMapper.toDto(savedUrl);
    }

    @Transactional
    public String getUrlByHash(String hash) {
        Optional<String> urlFromCacheOptional = urlCacheRepository.get(hash);

        if (urlFromCacheOptional.isPresent()) {
            return urlFromCacheOptional.get();
        }

        Object lock = locks.computeIfAbsent(hash, key -> new Object());
        synchronized (lock) {
            try {
                Optional<String> doubleCheckedCachedUrl = urlCacheRepository.get(hash);
                return doubleCheckedCachedUrl.orElseGet(() -> urlRepository.findByHash(hash)
                        .map(url -> {
                            String redirectUrl = url.getUrl();
                            urlCacheRepository.save(hash, redirectUrl, secondaryTimeout);
                            return redirectUrl;
                        })
                        .orElseThrow(() -> new EntityNotFoundException("Could not find this redirecting url")));
            } finally {
                locks.remove(hash);
            }
        }
    }
}
