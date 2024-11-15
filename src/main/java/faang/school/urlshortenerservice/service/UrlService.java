package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.builder.UrlBuilder;
import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.ResourceNotFoundException;
import faang.school.urlshortenerservice.repository.HashJdbcRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlService {
    private final HashCache hashCache;
    private final HashJdbcRepository hashJdbcRepository;
    private final UrlBuilder urlBuilder;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;

    @Value("${scheduler.lifetime_days}")
    private int lifetimeDays;

    @Transactional
    public String createHashUrl(String uri) {
        String hash = hashCache.getHash();
        Url url = Url.builder()
                .hash(hash)
                .url(uri)
                .createdAt(LocalDateTime.now())
                .build();

        urlRepository.save(url);
        urlCacheRepository.save(url);

        return urlBuilder.makeUrl(url.getHash());
    }

    @Transactional
    public void removeExpiredUrls() {
        LocalDate expireDate = LocalDate.now().minusDays(lifetimeDays);
        List<String> hashes = urlRepository.getAndDeleteUrlsByDate(expireDate);
        hashJdbcRepository.batchInsert(hashes);
        urlCacheRepository.deleteHashes(hashes);
    }

    public String getOriginalUrl(String hash) {
        String originalUrl = urlCacheRepository.findByHash(hash);

        if (originalUrl != null) {
            return originalUrl;
        }

        Url url = urlRepository.findById(hash).orElseThrow(() -> new ResourceNotFoundException(hash));

        return url.getUrl();
    }
}
