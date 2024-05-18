package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCash;
import faang.school.urlshortenerservice.cache.LocalCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCash hashCache;
    private final LocalCache localCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    @Value("${url}")
    private String baseUrl;

    public String createShortenUrl(String longUrl) {
        if (!isValidUrl(longUrl)) {
            throw new IllegalArgumentException("Invalid URL");
        }
        String hash = hashCache.getHash(longUrl);
        if (hash != null) {
            return baseUrl + hash;
        }
        hash = localCache.getHash();
        Url url = Url.builder()
                .url(longUrl)
                .hash(hash)
                .build();
        hashCache.putHash(longUrl, hash);//Вот как лучше оставилять такой компонент
        urlRepository.save(url);
        urlCacheRepository.save(hash, longUrl);//Или репозиторий nosql выгледит лучше
        return baseUrl + hash;
    }

    private boolean isValidUrl(String url) {
        String[] schemes = {"http", "https"};
        UrlValidator urlValidator = new UrlValidator(schemes);
        return urlValidator.isValid(url);
    }
}
