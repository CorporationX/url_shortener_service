package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UrlCache {

    private final UrlRepository urlRepository;

    @Cacheable(value = "urlCache", key = "#hash")
    public String getUrlByHash(String hash) {
        return urlRepository.findByHash(hash)
                .map(Url::getUrl)
                .orElseThrow(() -> new UrlNotFoundException(
                        String.format("URL для хеша: %s не найден", hash)));
    }

    @Cacheable(value = "urlCache", key = "#url", unless = "#result == null")
    public String getHashByUrl(String url) {
        return urlRepository.findByUrl(url)
                .map(Url::getHash)
                .orElseGet(() -> {
                    log.warn("Хэш для URL: {} не найден", url);
                    return null;
                });
    }

    @Caching(put = {
            @CachePut(value = "urlCache", key = "#url.hash"),
            @CachePut(value = "urlCache", key = "#url.url")})
    public Url saveUrl(Url url) {
        return urlRepository.save(url);
    }
}
