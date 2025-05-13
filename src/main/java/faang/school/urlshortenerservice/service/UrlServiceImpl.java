package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static faang.school.urlshortenerservice.exception.ErrorMessages.URL_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;

    @Value("${shortener.domain}")
    private String domain;

    @Override
    public String getOriginalUrl(String hash) {
        return urlCacheRepository.findByHash(hash).or(() -> {
                    Optional<String> originalUrl = urlRepository.findByHash(hash);
                    originalUrl.ifPresent(url -> urlCacheRepository.save(hash, url));
                    return originalUrl;
                })
                .orElseThrow(() -> new UrlNotFoundException(URL_NOT_FOUND));
    }

    @Override
    public String createShortUrl(String originalUrl) {
        String hash = hashCache.getHash();
        saveUrlMapping(hash, originalUrl);
        return buildShortUrl(hash);
    }

    private void saveUrlMapping(String hash, String originalUrl) {
        urlRepository.save(hash, originalUrl);
        urlCacheRepository.save(hash, originalUrl);
    }

    private String buildShortUrl(String hash) {
        return String.format("%s/%s", domain, hash);
    }
}

