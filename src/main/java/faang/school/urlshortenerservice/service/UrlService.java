package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.model.Url;
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
        String hash = hashCache.getHash().getHash();
        Url urlEntity = Url.builder()
                .hash(hash)
                .url(url)
                .build();
        urlRepository.save(urlEntity);
        urlCacheRepository.save(url, hash);

        return hash;
    }

    @Cacheable(value = "urls", key = "#hash")
    public String getUrl(String hash) {
        return urlRepository.findById(hash)
                .orElseThrow(() -> new EntityNotFoundException("Ссылки для такого хэша не существует"))
                .getUrl();
    }
}
