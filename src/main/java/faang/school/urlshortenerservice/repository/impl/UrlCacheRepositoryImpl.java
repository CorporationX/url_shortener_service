package faang.school.urlshortenerservice.repository.impl;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UrlCacheRepositoryImpl implements UrlCacheRepository {

    private static final String URL_CACHE_GROUP = "URL_CACHE_GROUP";
    private static final String URL_NOT_FOUND_MESSAGE = "Url not found";

    private final UrlRepository urlRepository;

    @CachePut(value = URL_CACHE_GROUP, key = "#url.hash")
    @Override
    public Url saveUrl(Url url) {
        return urlRepository.save(url);
    }

    @CachePut(value = URL_CACHE_GROUP, key = "#hash")
    @Override
    public Url findUrlByHash(String hash) {
        return urlRepository.findByHash(hash).orElseThrow(() -> new UrlNotFoundException(URL_NOT_FOUND_MESSAGE));
    }

    @CachePut(value = URL_CACHE_GROUP, key = "#url")
    @Override
    public Url findUrl(String url) {
        return urlRepository.findHashByUrl(url).orElse(null);
    }

    @CachePut(value = URL_CACHE_GROUP, key = "#expireDate")
    @Override
    public List<Url> pollExpires(LocalDateTime expireDate) {
        return urlRepository.pollOldUrls(expireDate);
    }

}
