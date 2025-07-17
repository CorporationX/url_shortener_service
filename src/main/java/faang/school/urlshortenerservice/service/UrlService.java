package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.HashCache;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.JdbcUrlRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final JdbcUrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    @Value("${base-url}")
    private String baseUrl;

    @Transactional
    public String generateShortUrl(String longUrl) {
        String candidate = hashCache.getHash();
        String hash = urlRepository.saveOrGet(candidate, longUrl, LocalDateTime.now());
        urlCacheRepository.putUrl(hash, longUrl);
        return baseUrl + "/" + hash;
    }

    @Transactional(readOnly = true)
    public String getLongUrl(String hash) {
        String fromCache = urlCacheRepository.getUrl(hash);
        if (fromCache != null) {
            return fromCache;
        }

       String fromDB = urlRepository
               .findUrlByHash(hash)
               .orElseThrow(()
               -> new UrlNotFoundException("Url for hash %s not found".formatted(hash)));

        urlCacheRepository.putUrl(hash, fromDB);
        return fromDB;
    }
}
