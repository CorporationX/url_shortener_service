package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;
    private final HashService hashService;

    @Transactional
    public String createShortUrl(String originalUrl) {
        String hash = hashService.getHash();

        if (hash == null) {
            hash = hashCache.getHashForUrl(originalUrl);
        }

        if (urlRepository.findByHash(hash).isPresent()) {
            return "http://short.url/" + hash;
        }

        Url url = new Url(hash, originalUrl, LocalDateTime.now());
        urlRepository.save(url);
        urlCacheRepository.save(url);

        return "http://short.url/" + hash;
    }
}