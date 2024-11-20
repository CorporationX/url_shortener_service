package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.config.cache.CacheProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final CacheProperties cacheProperties;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public void releaseExpiredHashes() {
        log.info("start moderateDB");

        List<String> existingHashes = urlRepository.getHashesAndDeleteExpiredUrls(cacheProperties.getNonWorkingUrlTime());
        log.info("get {} existingHashes", existingHashes.size());

        hashRepository.saveAllHashesBatched(existingHashes.stream()
                .map(Hash::new)
                .toList());

        log.info("finish moderateDB");
    }

    public String getLongUrl(String hash) {
        log.info("start getLongUrl with hash: {}", hash);

        Url url = urlCacheRepository.findUrlByHash(hash)
                .orElseGet(() -> urlRepository.findUrlByHash(hash)
                        .orElseThrow(() -> new EntityNotFoundException("Long url for: " + hash + " - does not exist")));

        log.info("finish getLongUrl with longUrl: {}", url.getUrl());
        return url.getUrl();
    }
}
