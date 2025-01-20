package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.HashCache;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;

    public String createShortUrl(String longUrl, long userId) {
        String hash = hashCache.getHash();
        Url url = Url.builder()
                .hash(hash)
                .url(longUrl)
                .createdAt(LocalDateTime.now())
                .build();
        urlRepository.save(url);
        urlCacheRepository.save(hash, longUrl);
        return hash;
    }

    public String getOriginalUrl(String hash, long userId) {
        return urlCacheRepository.get(hash)
                .or(() -> urlRepository.findById(hash).map(Url::getUrl))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "URL not found for hash: " + hash));
    }
}


