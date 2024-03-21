package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cach.LocalCache;
import faang.school.urlshortenerservice.entity.AssociationHashUrl;
import faang.school.urlshortenerservice.entity.UrlCache;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final LocalCache localCache;
    private final UrlRepository urlRepository;
    private final UrlCashRepository urlCashRepository;
    private final HashRepository hashRepository;

    @Value("${static-url.url}")
    private String staticUrl;

    @Transactional
    public String getHash(String url) {
        UrlCache urlCacheFromRedis = urlCashRepository.findByUrl(url);
        if (urlCacheFromRedis != null) {
            return staticUrl + urlCacheFromRedis.getHash();
        }
        String hash = localCache.getHash();
        AssociationHashUrl associationHashUrl = AssociationHashUrl.builder()
                .hash(hash)
                .url(url)
                .createdAt(LocalDateTime.now()).build();
        UrlCache urlCache = new UrlCache(hash, url);
        urlRepository.save(associationHashUrl);
        urlCashRepository.save(urlCache);
        log.info("Url and hash are saved: {}", associationHashUrl);
        log.info("Url and hash are saved in Redis: {}", urlCache.getHash());
        return staticUrl + hash;
    }

    @Transactional(readOnly = true)
    public String getLongUrl(String hash) {
        UrlCache urlCache = urlCashRepository.findById(hash).orElse(null);
        if (urlCache != null) {
            log.info("URL has found in Redis cash");
            return urlCache.getUrl();
        }
        AssociationHashUrl associationHashUrl = urlRepository.findByHash(hash)
                .orElseThrow(() -> {
                    var ex = new EntityNotFoundException("Url not found in database for hash: " + hash);
                    log.error("Url not found in database", ex);
                    return ex;
                });
        log.info("URL has found in data base: {}", associationHashUrl);
        return associationHashUrl.getUrl();
    }

    @Transactional
    @Async("executorService")
    public void deleteOldUrls() {
        boolean satisfactionCondition = urlRepository.existsRecordsOlderThanOneYear();
        if (satisfactionCondition) {
            List<String> hashes = urlRepository.deleteAndReturnOldUrls();
            hashRepository.save(hashes);
            log.info("Hashes saved as free");
        }
    }
}
