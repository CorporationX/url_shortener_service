package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.model.entity.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    @Transactional
    public String createShortUrl(String originalUrl) {
        String hash = hashCache.getHashForUrl(originalUrl);

        if (urlRepository.findByHash(hash).isPresent()) {
            return "http://short.url/" + hash;
        }

        Url url = new Url(hash, originalUrl, LocalDateTime.now());
        urlRepository.save(url);

        urlCacheRepository.save(url);

        return "http://short.url/" + hash;
    }
}