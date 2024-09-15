package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepositoryImpl {

    private final UrlRepository urlRepository;

    @CachePut(value = "originalUrl", key = "hash")
    public String save(String hash, String originalUrl) {
        Url saveUrl = urlRepository.create(hash, originalUrl);
        return saveUrl.getHash();
    }

    @Cacheable(value = "originalUrl", key = "hash")
    public String getUrl(String hash) {
        return urlRepository.getUrlByHash(hash);
    }
}
