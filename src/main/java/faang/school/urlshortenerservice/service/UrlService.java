package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.EmptyCacheException;
import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Value("${spring.url-service.url}")
    private String shortUrl;

    public ShortUrlDto create(LongUrlDto urlDto) {
        String hash = getHash();
        Url url = Url.builder()
                .hash(hash)
                .url(urlDto.getUrl())
                .build();
        save(url);
        return toShortUrlDto(hash);
    }

    public void save(Url url) {
        urlRepository.save(url);
        urlCacheRepository.save(url.getHash(), url.getUrl());
    }

    private String getHash() {
        return hashCache.get()
                .orElseThrow(() -> new EmptyCacheException("Hash cache is empty"));
    }

    private ShortUrlDto toShortUrlDto(String hash) {
        return new ShortUrlDto(shortUrl + hash);
    }
}
