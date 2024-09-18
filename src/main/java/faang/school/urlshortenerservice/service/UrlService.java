package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashRepository hashRepository;
    private final HashCache hashCache;

    @Value("${app.base-url}")
    private String baseUrl;

    @Transactional
    public String shortenUrl(UrlDto urlDto){
        String existingHash = urlCacheRepository.getCacheValueByUrl(urlDto.getUrl());
        if (existingHash != null) {
            return String.format("%s/%s", baseUrl, existingHash);
        }

        existingHash = urlRepository.findHashByUrl(urlDto.getUrl());
        if (existingHash != null) {
            urlCacheRepository.save(existingHash, urlDto.getUrl());
            return String.format("%s/%s", baseUrl, existingHash);
        }

        String hash = hashCache.getHash();

        Url url = Url.builder()
                .hash(hash)
                .url(urlDto.getUrl())
                .createdAt(LocalDateTime.now())
                .build();
        urlRepository.save(url);
        urlCacheRepository.save(hash, url.getUrl());

        return String.format("%s/%s", baseUrl, hash);
    }

    public String getOriginalUrl(String hash) {
        String originalUrl = urlCacheRepository.getCacheValue(hash);
        if (originalUrl != null) {
            return originalUrl;
        }

        originalUrl = urlRepository.findById(hash)
                .map(Url::getUrl)
                .orElseThrow(() -> new UrlNotFoundException("URL not found for hash: " + hash));

        urlCacheRepository.save(hash, originalUrl);

        return originalUrl;
    }

    public int cleanOldUrls(Period period) {
        LocalDateTime cutoffDate = LocalDateTime.now().minus(period);
        List<String> freedHashes = urlRepository.deleteOldUrlsAndReturnHashes(cutoffDate);

        if (!freedHashes.isEmpty()) {
            List<Hash> hashEntities = freedHashes.stream()
                    .map(Hash::new)
                    .toList();
            hashRepository.saveAll(hashEntities);
            log.info("Successfully cleaned old URLs and freed {} hashes.", freedHashes.size());
        } else {
            log.info("No old URLs to clean");
        }
        return freedHashes.size();
    }
}
