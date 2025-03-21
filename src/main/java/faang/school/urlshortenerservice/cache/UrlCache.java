package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UrlCache {

    private final UrlRepository urlRepository;

    @Cacheable(value = "url", key = "#hash")
    public String getUrlByHash(String hash) {
        return urlRepository.findByHash(hash)
                .map(Url::getUrl)
                .orElseThrow(() -> new UrlNotFoundException(
                        String.format("URL для хеша: %s не найден", hash)));
    }

    @Cacheable(value = "hash", key = "#url")
    public String getHashByUrl(String url) {
        return urlRepository.findByUrl(url)
                .map(Url::getHash)
                .orElseGet(() -> {
                    log.info("Хэш для URL: {} не найден", url);
                    return null;
                });
    }

    @CachePut(value = "url", key = "#url.hash")
    public Map<String, String> saveUrl(Url url) {
        Map<String, String> cache = new HashMap<>();
        cache.put("hash", url.getHash());
        cache.put("url", url.getUrl());

        urlRepository.save(url);
        return cache;
    }
}
