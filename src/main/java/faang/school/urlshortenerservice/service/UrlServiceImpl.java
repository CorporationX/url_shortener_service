package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.request.UrlRequest;
import faang.school.urlshortenerservice.dto.response.UrlResponse;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.model.UrlCache;
import faang.school.urlshortenerservice.repository.jpa.UrlRepository;
import faang.school.urlshortenerservice.repository.redis.UrlCacheRepository;
import jakarta.persistence.EntityExistsException;
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

    @Value("${url.constants.days-to-remove}")
    private int daysToRemove;

    @Override
    @Transactional
    public List<String> deleteUnusedHashes() {
        LocalDate today = LocalDate.now();
        List<String> urls = urlRepository.releaseUnusedHashesFrom(today.minusDays(daysToRemove));
        log.info("Deleted {} unused urls", urls.size());
        return urls;
    }

    @Override
    @Transactional
    public void updateUrls(List<String> hashes) {
        List<Url> urls = urlRepository.findAllById(hashes);
        urls.forEach(url -> url.setCacheDate(LocalDate.now()));
        urlRepository.saveAll(urls);
        log.info("Updated {} urls", urls.size());
    }

    @Retryable(retryFor = DataIntegrityViolationException.class,
            maxAttemptsExpression = "${retryable.max-attempts}",
            backoff = @Backoff(delayExpression = "${retryable.delay}"))
    @Override
    @Transactional
    public UrlResponse shortenUrl(UrlRequest request) {
        Optional<Url> savedUrl = urlRepository.findUrlByUrl(request.url());
        if (savedUrl.isPresent()) {
            Url url = savedUrl.get();
            throw new EntityExistsException("Url already exists url: %s, hash: %s"
                    .formatted(url.getUrl(), url.getHash()));
        }
        Url url = Url.builder()
                .url(request.url())
                .hash(hashCache.getHash())
                .build();
        url = urlRepository.save(url);
        urlCacheRepository.save(urlMapper.toUrlCache(url));
        log.debug("Saved url: {}", url);
        return new UrlResponse(url.getHash());
    }

    @Override
    public String getUrl(String hash) {
        Optional<UrlCache> urlCache = urlCacheRepository.findById(hash);
        if (urlCache.isPresent()) {
            return urlCache.get().getUrl();
        }
        Url url = urlRepository.findById(hash)
                .orElseThrow(() -> new EntityNotFoundException("Url not found with hash: %s"
                        .formatted(hash)));
        urlCacheRepository.save(urlMapper.toUrlCache(url));
        return url.getUrl();
    }
}
