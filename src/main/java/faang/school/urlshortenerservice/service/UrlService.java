package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.url.ShortUrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    @Value("${url.shortener-service.address}")
    private String serverAddress;

    @Transactional
    public String shortenUrl(String longUrl) {
        String hash = hashCache.getHash().getHash();

        try {
            urlRepository.save(hash, longUrl);
        } catch (Exception e) {
            log.error("Error while saving URL in repository: {}", e.getMessage());
        }

        try {
            urlCacheRepository.save(hash, longUrl);
        } catch (Exception e) {
            log.error("Error while saving URL in cache: {}", e.getMessage());
        }

        return serverAddress + hash;
    }

    public String getOriginalURL(String hash) {
        String originalURL = urlCacheRepository.get(hash);

        if (originalURL == null) {
            Url url = urlRepository.findByHash(hash);
            if (url != null) {
                originalURL = url.getUrl();
                urlCacheRepository.save(hash, originalURL);
            } else {
                throw new ShortUrlNotFoundException("Short URL not found for hash: " + hash);
            }
        }

        return originalURL;
    }
}