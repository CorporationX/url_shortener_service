package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.UrlHash;
import faang.school.urlshortenerservice.model.UrlHashRedis;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
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
}
