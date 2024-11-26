package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.request.UrlRequest;
import faang.school.urlshortenerservice.dto.response.UrlResponse;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.model.UrlCache;
import faang.school.urlshortenerservice.repository.jpa.UrlRepository;
import faang.school.urlshortenerservice.repository.redis.UrlCacheRepository;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;
    private final UrlMapper urlMapper;

    @Value("${url.constants.days-to-remove:365}")
    private int daysToRemove;

    @Override
    @Transactional
    public List<String> deleteUnusedUrls() {
        LocalDate today = LocalDate.now();
        List<String> urls = urlRepository.releaseUnusedHashesFrom(today.minusDays(daysToRemove));
        log.info("Deleted {} unused urls", urls.size());
        return urls;
    }

    @Override
    @Transactional
    public void updateUrls(List<String> hashes) {
        List<Url> urls = urlRepository.findAllById(hashes);
        urls.forEach(url -> url.setLastTtlExpirationDate(LocalDate.now()));
        urlRepository.saveAll(urls);
        log.info("Updated {} urls", urls.size());
    }

    @Retryable(retryFor = DataIntegrityViolationException.class,
            maxAttemptsExpression = "${retryable.max-attempts:5}",
            backoff = @Backoff(delayExpression = "${retryable.delay:5000}"))
    @Override
    @Transactional
    public UrlResponse shortenUrl(UrlRequest request) {
        Optional<Url> savedUrl = urlRepository.findUrlByUrl(request.url());
        if (savedUrl.isPresent()) {
            Url url = savedUrl.get();
            return new UrlResponse(url.getHash());
        }
        String hash = hashCache.getHash();
        if (hash == null) {
            throw new RuntimeException("No available hashes");
        }
        Url url = Url.builder()
                .url(request.url())
                .hash(hash)
                .build();
        try {
            url = urlRepository.save(url);
        } catch (DataIntegrityViolationException e) {
            log.warn("Hash collision detected, retrying...");
            throw e;
        }
        urlCacheRepository.save(urlMapper.toUrlCache(url));
        log.debug("Saved url: {}", url);
        return new UrlResponse(url.getHash());
    }

    @Override
    public String getUrl(String hash) {
        Optional<UrlCache> urlCache = urlCacheRepository.findById(hash);
        if (urlCache.isPresent()) {
            urlCacheRepository.save(urlCache.get());
            return urlCache.get().getUrl();
        }
        Url url = urlRepository.findById(hash)
                .orElseThrow(() -> new EntityNotFoundException("Url not found with hash: %s"
                        .formatted(hash)));
        urlCacheRepository.save(urlMapper.toUrlCache(url));
        return url.getUrl();
    }
}