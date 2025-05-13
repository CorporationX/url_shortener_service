package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.enity.Url;
import faang.school.urlshortenerservice.hash.LocalHash;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlServiceImplV1 implements UrlService{
    private final LocalHash localHash;
    private final UrlRepository urlRepository;
    private final CacheManager cacheManager;

    @Value(value = "${hash.time.saving.day:100}")
    private int savingDays;

    @Override
    @CachePut(value = "urlToHash", key = "#url")
    public String save(String url) {
        String hash = urlRepository.findByUrl(url)
                .orElseGet(() -> urlRepository.save(Url.builder()
                        .url(url)
                        .hash(localHash.getHash())
                        .last_get_at(LocalDateTime.now())
                        .build()))
                .getHash();
        cacheManager.getCache("hashToUrl").put(hash, url);

        return hash;
    }

    @Override
    @Cacheable(value = "hashToUrl", key = "#hash", unless = "#result == null")
    public String get(String hash) {
        return urlRepository.findByHash(hash)
                .orElseThrow(() -> new EntityNotFoundException("url by hash " + hash + " does not exists"))
                .getUrl();

    }

    @Override
    @Cacheable(value = "urlToHash", key = "#url", unless = "#result == null")
    public String getHash(String url) {
        return urlRepository.findByUrl(url)
                .orElseThrow(() -> new EntityNotFoundException("url " + url + " does not exists"))
                .getHash();
    }

    @Override
    public void deleteUnusedUrl() {
        urlRepository.deleteUnusedUrl(LocalDateTime.now().minusDays(savingDays));
    }
}
