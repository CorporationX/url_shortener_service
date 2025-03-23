package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Value("${short.url.domain:http://localhost:8080/}")
    private String shortUrlDomain;

    public String createShortUrl(UrlRequestDto urlRequest) {
        String longUrl = urlRequest.getOriginalUrl();
        String hash = hashCache.getHash();
        urlRepository.saveUrl(hash, longUrl);
        urlCacheRepository.saveUrl(hash, longUrl);
        return shortUrlDomain + hash;
    }

    public String getLongUrl(String hash) {
        String longUrl = urlCacheRepository.findByHash(hash);
        if (longUrl != null) {
            return longUrl;
        }
        longUrl = urlRepository.findByHash(hash);
        if (longUrl != null) {
            urlCacheRepository.saveUrl(hash, longUrl);
            return longUrl;
        }
        throw new UrlNotFoundException("No URL found for hash: " + hash);
    }
}
