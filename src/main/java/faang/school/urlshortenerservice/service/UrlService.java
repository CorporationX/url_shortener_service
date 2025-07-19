package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repo.UrlCache;
import faang.school.urlshortenerservice.repo.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCache urlCacheRepository;

    @Transactional
    public String createShortUrl(String originalUrl) {
        String hash = hashCache.getHash();

        Url url = Url.builder()
                .originalUrl(originalUrl)
                .hash(hash)
                .build();

        urlRepository.save(url);
        urlCacheRepository.save(hash, originalUrl);

        log.info("Created short URL: {} for original: {}", hash, originalUrl);
        return hash;
    }

    public Optional<String> getOriginalUrl(String hash) {

        String cachedUrl = urlCacheRepository.get(hash);
        if (cachedUrl != null) {
            return Optional.of(cachedUrl);
        }

        return urlRepository.findByHash(hash)
                .map(Url::getOriginalUrl);
    }

}