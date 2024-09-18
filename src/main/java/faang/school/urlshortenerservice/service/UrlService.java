package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cashe.HashCash;
import faang.school.urlshortenerservice.dto.UrlDtoRequest;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.repository.redis.UrlCacheRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashCash hashCash;
    private final UrlCacheRepository urlCacheRepository;
    @Value("${hash.url_prefix}")
    @Setter
    private String urlPrefix;

    @Transactional
    public String getShortUrl(UrlDtoRequest request) {
        String hash = hashCash.getHash();
        saveShortUrl(hash, request);
        return urlPrefix + hash;
    }

    @Transactional(readOnly = true)
    public String getUrlFromHash(String hash) {
        Optional<String> cacheUrl = urlCacheRepository.getUrlByHash(hash);
        if (cacheUrl.isPresent()) {
            return cacheUrl.get();
        }
        Url entity = urlRepository.findById(hash)
                .orElseThrow(() -> new EntityNotFoundException("Url with " + hash + " not found!"));
        return entity.getUrl();
    }

    private void saveShortUrl(String hash, UrlDtoRequest request) {
        Url url = Url.builder()
                .url(request.getUrl())
                .hash(hash)
                .build();
        urlRepository.save(url);
        urlCacheRepository.saveUrlByHash(hash, request.getUrl());
    }
}
