package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepositoryImpl {

    private final UrlRepository urlRepository;

    @CachePut(value = "originalUrl", key = "#hash")
    public Url save(String hash, String originalUrl) {
        return urlRepository.create(hash, originalUrl);
    }

    public String getUrl(String hash) {
        Url urlEntity = urlRepository.findByHash(hash)
                .orElseThrow(() -> new NotFoundException("Url for hash " + hash + " is not found"));
        return urlEntity.getOriginalUrl();
    }
}