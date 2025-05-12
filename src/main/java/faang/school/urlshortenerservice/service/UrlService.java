package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
            throw new IllegalArgumentException("URL must not be null or empty");
        }

        String hash = hashCache.findHash();
        if (hash == null) {
            log.error("Failed to generate hash");
            throw new RuntimeException("Could not generate hash");
        }

        try {
            urlRepository.insertUrl(hash, urlDto.url());
            urlCacheRepository.save(hash, urlDto.url());
            log.info("Successfully shortened URL: {} to hash: {}", urlDto.url(), hash);
        } catch (Exception e) {
            log.error("Error occurred while shortening URL: {}", e.getMessage(), e);
            throw new RuntimeException("Could not shorten URL", e);
        }

        return hash;
    }
}
