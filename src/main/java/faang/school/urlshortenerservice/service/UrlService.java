package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.UrlHash;
import faang.school.urlshortenerservice.model.UrlHashRedis;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
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
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public String createShortLink(String url) {
        Optional<UrlHash> urlHash = urlRepository.findByUrl(url);
        if (urlHash.isPresent()) {
            log.info("Url already exists in DB. Return saved hash");
            return urlHash.get().getHash();
        }
        String hash = hashCache.getHash();
        log.info("Hash: {}", hash);
        urlRepository.save(UrlHash.builder()
                .hash(hash)
                .url(url)
                .build());
        urlCacheRepository.save(UrlHashRedis.builder()
                .id(hash)
                .url(url)
                .build());
        return hash;
    }

    public String getOriginalUrlByHash(String hash) {
        Optional<UrlHashRedis> urlHashRedis = urlCacheRepository.findById(hash);
        if (urlHashRedis.isPresent()) {
            log.info("Url: {} found in Redis by hash: {}", urlHashRedis.get().getUrl(), urlHashRedis.get().getId());
            return urlHashRedis.get().getUrl();
        } else {
            return urlRepository.findById(hash).orElseThrow(() -> {
                String errMessage = String.format("Original URL not found by hash: %s in DB", hash);
                log.info(errMessage);
                return new EntityNotFoundException(errMessage);
            }).getUrl();
        }
    }
}
