package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.HashCache;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final HashCache hashCache;

    @Cacheable(cacheNames = "UrlService::createShortUrl", key = "#url")
    public String createShortUrl(String url, long userId) {
        String hash = hashCache.getHash();
        Url longUrl = Url.builder()
                .hash(hash)
                .url(url)
                .createdAt(LocalDateTime.now())
                .build();
        urlRepository.save(longUrl);
        return hash;
    }

    @Cacheable(cacheNames = "UrlService::getOriginalUrl", key = "#hash")
    public String getOriginalUrl(String hash, long userId) {
        return urlRepository.findById(hash)
                .map(Url::getUrl)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "URL not found for hash: " + hash
                ));
    }
}


