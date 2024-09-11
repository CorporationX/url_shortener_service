package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepositoryImpl {

    private final UrlRepository urlRepository;

    @CachePut()
    public String save(String hash, String originalUrl) {
        Url saveUrl = urlRepository.create(hash, originalUrl);
        return saveUrl.getHash();
    }
}
