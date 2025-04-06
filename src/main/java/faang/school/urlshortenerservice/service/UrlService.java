package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    @Transactional
    public String getHash(String url) {
        String hash = hashCache.getHash();
        Url entity = Url.builder()
                .hash(hash)
                .url(url)
                .build();
        urlRepository.save(entity);
        urlCacheRepository.save(url, hash);

        return hash;
    }

    @Cacheable(value = "urls", key = "#hash")
    public String getOriginalUrl(String hash) {
        return urlRepository.findById(hash)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Ссылки с хэшом %s не существует!", hash)))
                .getHash();
    }
}
