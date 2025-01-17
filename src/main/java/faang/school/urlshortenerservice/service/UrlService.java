package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.UrlEntity;
import faang.school.urlshortenerservice.cache.UrlCacheRepository;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    @Value("${server.url}")
    private String domain;

    @Transactional
    public String createShortUrl(String longUrl) {
        String hash = hashCache.getHash();

        UrlEntity urlEntity = new UrlEntity(hash, longUrl, LocalDateTime.now());
        urlRepository.save(urlEntity);

        urlCacheRepository.saveUrl(hash, longUrl);

        return domain + "/" + hash;
    }

    public String getOriginalUrl(String hash) {
        String cachedUrl = urlCacheRepository.getUrl(hash);
        if (cachedUrl != null) {
            return cachedUrl;
        }

        return urlRepository.findById(hash)
                .map(UrlEntity::getUrl)
                .orElseThrow(() -> new UrlNotFoundException(hash));
    }

    public List<String> deleteOldUrlsAndGetHashes(LocalDate date) {
        return urlRepository.deleteOldUrlsAndGetHashes(date);
    }
}


