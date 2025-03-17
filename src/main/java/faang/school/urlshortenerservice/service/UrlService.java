package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.cache.UrlCache;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.publisher.UrlEventPublisher;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final UrlCache urlCache;
    private final HashCache hashCache;
    private final UrlEventPublisher urlEventPublisher;

    @Value("${hash.cleanup.period}")
    private Period hashCleanupPeriod;

    @Value("${shortener.base-url}")
    private String baseUrl;

    @Transactional
    public UrlResponseDto createShortUrl(UrlRequestDto urlRequestDto,
                                         String userId) {
        String hash = hashCache.getHash();
        Url url = Url.builder()
                .hash(hash)
                .url(urlRequestDto.getOriginalUrl())
                .build();
        urlRepository.save(url);
        urlCache.saveUrlByHash(hash, url.getUrl());
        urlEventPublisher.publishShortUrlCreated(hash, url.getUrl(), userId);

        return UrlResponseDto.builder()
                .shortUrl(baseUrl + hash)
                .build();
    }

    public String getUrlFromHash(String hash) {
        return urlCache.getUrlByHash(hash)
                .orElseGet(() -> urlRepository
                        .findByHash(hash)
                        .map(url -> {
                            urlCache.saveUrlByHash(hash, url.getUrl());
                            return url.getUrl();
                        })
                        .orElseThrow(() -> new UrlNotFoundException(
                                String.format("URL для хеша: %s не найден", hash))));
    }

    @Transactional
    public void deleteOldUrl() {
        LocalDateTime fromDate = LocalDateTime.now().minus(hashCleanupPeriod);
        List<Hash> hashes = urlRepository.removeOldUrlAndGetFreeHashes(fromDate);

        if (!hashes.isEmpty()) {
            hashRepository.saveAll(hashes);
            log.info("Очистка завершена: {} хешей теперь свободны.", hashes.size());
        }
    }
}
