package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {
    @Value("${shortener.domain}")
    private String domain;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;
    private final HashRepository hashRepository;

    public UrlResponseDto createShortUrl(String longUrl) {
        var hash = hashCache.getHash();
        var url = Url.builder().hash(hash).url(longUrl).build();

        urlRepository.save(url);
        urlCacheRepository.save(hash, longUrl);

        String shortUrl = buildShortUrl(hash);

        return UrlResponseDto.builder()
                .originalUrl(longUrl)
                .shortUrl(shortUrl)
                .build();
    }

    public String getOriginalUrl(String hash) {
        String cachedUrl = urlCacheRepository.find(hash);
        if (cachedUrl != null) {
            return cachedUrl;
        }

        return urlRepository.findByHash(hash)
                .map(Url::getUrl)
                .orElseThrow(() -> new UrlNotFoundException("URL не найден для хэша: " + hash));
    }

    public void removeOldUrls(LocalDateTime cutoffDate) {
        urlRepository.deleteOldUrlsAndReturnHashes(cutoffDate)
                .forEach(hash -> hashRepository.save(Hash.builder().hash(hash).build()));
    }

    private String buildShortUrl(String hash) {
        return domain + hash;
    }
}
