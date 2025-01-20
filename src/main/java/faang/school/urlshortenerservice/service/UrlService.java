package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    public String createShortUrl(UrlDto urlDto) {
        String hashFromDB = urlRepository.findHashByUrl(urlDto.getUrl());
        if (hashFromDB != null) {
            urlCacheRepository.save(hashFromDB, urlDto.getUrl());
            return getShortUrl(hashFromDB);
        }

        String hash = hashCache.getHash();

        Url url = Url.builder()
                .hash(hash)
                .url(urlDto.getUrl())
                .createdAt(LocalDateTime.now())
                .build();

        urlRepository.save(url);
        urlCacheRepository.save(hash, url.getUrl());

        return getShortUrl(hash);
    }

    public String getOriginalUrl(String hash) {
        String originalUrlFromCache = urlCacheRepository.getCacheValueByHash(hash);
        if (originalUrlFromCache != null) {
            return originalUrlFromCache;
        }

        String originalUrl = urlRepository.findById(hash)
                .map(Url::getUrl)
                .orElseThrow(() ->
                        new UrlNotFoundException("URL not found in DB by hash: " + hash)
                );

        urlCacheRepository.save(hash, originalUrl);

        return originalUrl;
    }

    public long cleanUrls(Period period) {
        LocalDateTime cutoff = LocalDateTime.now().minus(period);

        List<String> releasedHashes = urlRepository.deleteUrlsAndReturnHashList(cutoff);

        if(releasedHashes.isEmpty()) {
            log.info("Nothing was released!");
            return 0L;
        }

        List<Hash> hashEntities = releasedHashes.stream()
                .map(Hash::new)
                .collect(Collectors.toList());

        hashRepository.saveAll(hashEntities);

        return releasedHashes.size();
    }

    private String getShortUrl(String hash) {
        return String.format("%s/%s", baseUrl, hash);
    }
}
