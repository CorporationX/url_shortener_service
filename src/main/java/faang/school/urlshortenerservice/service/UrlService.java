package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.component.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.interfaces.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public String shortenUrl(String originalUrl) {
        String existingHash = urlCacheRepository.findHashByUrl(originalUrl);
        if (existingHash != null) {
            log.info("Found hash in cache for URL: {}", originalUrl);
            return "http://short.url/" + existingHash;
        }

        Optional<UrlDto> existingUrl = urlRepository.findByUrl(originalUrl);
        if (existingUrl.isPresent()) {
            String hash = existingUrl.get().hash();
            urlCacheRepository.save(hash, originalUrl);
            log.info("Found existing URL in database, hash: {}", hash);
            return "http://short.url/" + hash;
        }

        String hash = hashCache.getHash();
        if (hash == null) {
            log.error("Failed to generate hash for URL: {}", originalUrl);
            throw new RuntimeException("Failed to generate hash: HashCache is empty");
        }

        if (urlRepository.findByHash(hash).isPresent()) {
            log.error("Hash collision detected for URL: {}", originalUrl);
            throw new RuntimeException("Hash already exists");
        }

        urlRepository.save(hash, originalUrl);
        log.info("Saved new URL with hash: {} for URL: {}", hash, originalUrl);

        urlCacheRepository.save(hash, originalUrl);

        return "http://short.url/" + hash;
    }

    public String getOriginalUrl(String hash) {
        log.info("Looking for URL by hash: {}", hash);

        String urlFromCache = urlCacheRepository.findByHash(hash);
        if (urlFromCache != null) {
            log.info("URL found in Redis for hash: {}", hash);
            return urlFromCache;
        }

        Optional<UrlDto> urlFromDb = urlRepository.findByHash(hash);
        if (urlFromDb.isPresent()) {
            String url = urlFromDb.get().url();
            log.info("URL found in database for hash: {}", hash);
            urlCacheRepository.save(hash, url);
            return url;
        }

        log.warn("URL not found for hash: {}", hash);
        throw new UrlNotFoundException("URL not found for hash: " + hash);
    }
}