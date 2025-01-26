package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.jpa.HashRepository;
import faang.school.urlshortenerservice.repository.redis.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.jpa.UrlRepository;
import faang.school.urlshortenerservice.service.config.HashCache;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Data
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final HashCache hashCache;
    private final UrlCacheRepository redisRepository;

    @Value("${spring.properties.domain}")
    private String domain;

    @Transactional
    public String createShortUrl(UrlDto urlDto) {
        validateUrl(urlDto.getUrl());
        Hash hash = hashCache.getHash();
        Url url = Url.builder()
                .hash(hash.getHash())
                .url(urlDto.getUrl())
                .createdAt(LocalDateTime.now()).build();
        urlRepository.save(url);
        log.info("Successfully saved the url in POSTGRESQL");
        redisRepository.save(url.getHash(), url.getUrl());
        log.info("Successfully saved the url in REDIS");
        return domain.concat(hash.getHash());
    }

    public String getUrl(String hash) {
        String url = redisRepository.getByHash(hash);
        if (url != null) {
            log.info("Successfully retrieved the url from REDIS");
            return url;
        }
        Url urlFromDb = urlRepository.findById(hash)
                .orElseThrow(() -> new RuntimeException("Hash not found"));
        return urlFromDb.getUrl();
    }

    @Transactional
    public void removeExpiredAndAddToHashRepo() {
        List<Url> expiredUrl = urlRepository.removeUrlsByExpired();
        if (expiredUrl.isEmpty()) {
            log.info("No url expired in the Database");
            return;
        }

        List<Hash> hashes = expiredUrl.stream()
                .map(url -> new Hash(url.getHash()))
                .toList();
        hashRepository.saveAll(hashes);
        log.info("Successfully removed expired urls and add them to HashCache");
    }

    private void validateUrl(String url) {
        try {
            new URL(url).toURI();
        } catch (Exception e) {
            log.error("Received a request with invalid url address", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
