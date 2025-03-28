package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.LocalHashCache;
import faang.school.urlshortenerservice.config.UrlProperties;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final LocalHashCache cache;
    private final UrlProperties urlProperties;
    private final CacheManager cacheManager;


    @Override
//    @CachePut(value = "urls", key = "#hash")
    @Transactional
    public ShortUrlDto createShortUrl(UrlDto urlDto) {
        log.info("Calling UrlServiceImpl#createShortUrl ...");
        String hash = cache.getHash();
        String originalUrl = urlDto.url();
        Url urlEntity = Url.builder()
                .hash(hash)
                .url(originalUrl)
                .build();
        urlRepository.save(urlEntity);

        cacheManager.getCache("urls").put(hash, urlDto);

        return new ShortUrlDto(urlProperties.getBaseShortUrl() + hash);
    }

    @Override
    @Cacheable(value = "urls", key = "#hash", unless = "#result == null")
    @Transactional(readOnly = true)
    public UrlDto getUrl(String hash) {
        log.info("Calling UrlServiceImpl#getUrl ...");
        return urlRepository.findById(hash)
                .map(Url::getUrl)
                .map(UrlDto::new)
                .orElseThrow(() -> new EntityNotFoundException("Url not found with hash: %s".formatted(hash)));
    }

    @Override
    @Transactional
    public void cleanExpiredUrls() {
        log.info("Cleaning of expired urls started...");
        List<String> hashesStr = urlRepository.cleanExpiredUrls();
        List<Hash> hashes = hashesStr.stream()
                .filter(it -> !it.isEmpty() && !it.isBlank())
                .map(Hash::new)
                .toList();

        hashRepository.saveAll(hashes);

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        Cache urlsCache = cacheManager.getCache("urls");
                        if (Objects.nonNull(urlsCache)) {
                            hashesStr.forEach(urlsCache::evict);
                        }
                        log.info("Cleaned {} URLs", hashesStr.size());
                    }
                }
        );
    }
}
