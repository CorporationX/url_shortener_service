package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.component.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exceptions.CacheOperationException;
import faang.school.urlshortenerservice.exceptions.HashGenerationException;
import faang.school.urlshortenerservice.exceptions.InvalidHashException;
import faang.school.urlshortenerservice.exceptions.InvalidUrlException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    public String shortenUrl(UrlDto urlDto) {

        if (urlDto == null || urlDto.url() == null || urlDto.url().isEmpty()) {
            log.error("Invalid URL provided: {}", urlDto);
            throw new InvalidUrlException("URL must not be null or empty");
        }

        String hash = hashCache.getHash();
        if (hash == null) {
            log.error("Failed to generate hash");
            throw new HashGenerationException("Could not generate hash");
        }

        try {
            urlRepository.insertUrl(hash, urlDto.url());
            urlCacheRepository.save(hash, urlDto.url());
            log.info("Successfully shortened URL: {} to hash: {}", urlDto.url(), hash);
        } catch (Exception e) {
            log.error("Error occurred while shortening URL: {}", e.getMessage(), e);
            throw new CacheOperationException("Could not shorten URL", e);
        }

        return hash;
    }

    public String getOriginalUrl(String hash) {
        if (hash == null || hash.isEmpty()) {
            log.error("Invalid hash provided: {}", hash);
            throw new InvalidHashException("Hash cannot be null or empty.");
        }
        Optional<String> cachedUrl = urlCacheRepository.findByHash(hash);
        return cachedUrl.orElseGet(() -> urlRepository.findUrlByHash(hash)
                .orElseThrow(() -> new EntityNotFoundException("URL not found for hash: " + hash)));
    }
}
