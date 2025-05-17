package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
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
            return "http://short.url/" + existingHash;
        }

        Optional<Url> existingUrl = urlRepository.findByUrl(originalUrl);
        if (existingUrl.isPresent()) {
            String hash = existingUrl.get().getHash();
            urlCacheRepository.save(hash, originalUrl);
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

        Url url = new Url();
        url.setHash(hash);
        url.setUrl(originalUrl);
        urlRepository.save(url);

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

        Optional<Url> urlFromDb = urlRepository.findByHash(hash);
        if (urlFromDb.isPresent()) {
            String url = urlFromDb.get().getUrl();
            log.info("URL found in database for hash: {}", hash);
            urlCacheRepository.save(hash, url);
            return url;
        }

        log.warn("URL not found for hash: {}", hash);
        throw new UrlNotFoundException("URL not found for hash: " + hash);
    }
}
