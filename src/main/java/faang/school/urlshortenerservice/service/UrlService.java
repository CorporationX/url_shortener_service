package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.EmptyCacheException;
import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.OriginalUrlDto;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.HashNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashService hashService;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;
    @Value("${spring.url-service.url}")
    private String shortUrl;

    @Async("hashGeneratorExecutor")
    @Transactional
    public void findAndDelete() {
        List<String> hashes = urlRepository.findAndDelete();
        if (!hashes.isEmpty()) {
            hashService.saveHashes(hashes);
        }
    }

    public ShortUrlDto create(OriginalUrlDto urlDto) {
        String hash = getHashFromCache();
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

    public OriginalUrlDto getUrlByHash(String hash) {
        Optional<String> optionalUrl = urlCacheRepository.getUrl(hash);
        if (optionalUrl.isPresent()) {
            return toLongUrlDto(optionalUrl.get());
        } else {
            Url url = getUrlByHashFromDB(hash);
            return toLongUrlDto(url.getUrl());
        }
    }

    private Url getUrlByHashFromDB(String hash) {
        return urlRepository.findByHash(hash)
                .orElseThrow(() -> new HashNotFoundException("-1", "Hash not found"));
    }

    private String getHashFromCache() {
        return hashCache.get()
                .orElseThrow(() -> new EmptyCacheException("-2", "Hash cache is empty"));
    }

    private ShortUrlDto toShortUrlDto(String hash) {
        return new ShortUrlDto(shortUrl + hash);
    }

    private OriginalUrlDto toLongUrlDto(String url) {
        return new OriginalUrlDto(url);
    }
}
