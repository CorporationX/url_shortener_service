package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.enity.FreeHash;
import faang.school.urlshortenerservice.enity.Url;
import faang.school.urlshortenerservice.exception.CacheNotFoundException;
import faang.school.urlshortenerservice.hash.LocalHash;
import faang.school.urlshortenerservice.properties.HashProperties;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.hash.HashService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final LocalHash localHash;
    private final UrlRepository urlRepository;
    private final HashService hashService;
    private final CacheManager cacheManager;
    private final HashProperties hashProperties;
    private final ExecutorService saveToCachePool;

    @Override
    @Transactional
    public HashDto save(String url) {
        String hash = localHash.getHash();
        urlRepository.save(Url.builder()
                .url(url)
                .hash(hash)
                .lastGetAt(LocalDateTime.now())
                .build());
        saveToCache(hash, url);
        return new HashDto(hash);
    }

    @Override
    @Transactional
    @Cacheable(value = "hashToUrl", key = "#hash", unless = "#result == null")
    public String get(String hash) {
        Url url = urlRepository.findByHash(hash)
                .orElseThrow(() -> new EntityNotFoundException("url by hash " + hash + " does not exists"));
        url.setLastGetAt(LocalDateTime.now());
        return url.getUrl();
    }

    @Override
    @Transactional
    public void freeUnusedHash() {
        hashService.saveAll(urlRepository.deleteAndGetUnusedUrl(
                LocalDateTime.now().minusDays(hashProperties.getSaving().getTime().toDays()),
                hashProperties.getGet().getCount()).stream()
                .map(Url::getHash)
                .map(FreeHash::new)
                .toList());
    }

    private void saveToCache(String hash, String url) {
        CompletableFuture.runAsync(() ->
                Optional.ofNullable(cacheManager.getCache(hashProperties.getCaches().getHashToUrl()))
                        .orElseThrow(() -> new CacheNotFoundException(
                                "cache with name " + hashProperties.getCaches().getHashToUrl() + "not found"))
                        .put(hash, url), saveToCachePool);
    }
}
