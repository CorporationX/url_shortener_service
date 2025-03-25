package faang.school.urlshortenerservice.repository.impl;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UrlCacheRepositoryImpl implements UrlCacheRepository {

    private final UrlRepository urlRepository;

    @CachePut(URL_CACHE_GROUP)
    @Override
    public Url saveUrl(Url url) {
        return urlRepository.save(url);
    }

    @Cacheable(URL_CACHE_GROUP)
    @Override
    public Url findByHash(String hash) {
        return urlRepository.findByHash(hash).orElseThrow(UrlNotFoundException::new);
    }

    @CacheEvict(URL_CACHE_GROUP)
    @Override
    public List<Url> pollExpires(LocalDateTime expireDate) {
        return urlRepository.pollOldUrls(expireDate);
    }

    private static final String URL_CACHE_GROUP = "URL_CACHE_GROUP";
}
