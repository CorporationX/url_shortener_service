package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.EmptyCacheException;
import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashService hashService;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;
    @Value("${spring.url-service.url}")
    private String shortUrl;

    @Transactional
    public void findAndDelete() {
        List<String> hashes = urlRepository.findAndDelete();
        if (!hashes.isEmpty()) {
            hashService.saveHashes(hashes);
        }
    }

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
