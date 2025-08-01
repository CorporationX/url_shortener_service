package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.LocalCache;
import faang.school.urlshortenerservice.dto.ShortenedUrlDto;
import faang.school.urlshortenerservice.dto.UrlShortenerDto;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.ShortenedUrl;
import faang.school.urlshortenerservice.redis.RedisFacade;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.ShortenedUrlRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlShortenerServiceImpl implements UrlShortenerService {

    private final LocalCache cache;
    private final ShortenedUrlRepository shortenedUrlRepository;
    private final RedisFacade redisFacade;
    private final HashRepository hashRepository;

    @Value("${spring.data.redis.cashingDuration}")
    private int cachingDuration;
    @Value("${static_url}")
    private String staticUrl;
    @Value("${hash-generation.batch_size}")
    private int batchSize;

    @Override
    @Transactional
    public ShortenedUrlDto create(UrlShortenerDto urlShortenerDto) {
        String hashToSave = cache.getHash();
        String longUrl = urlShortenerDto.getLongUrl();
        ShortenedUrl shortenedUrl = new ShortenedUrl();
        shortenedUrl.setLongUrl(longUrl);
        shortenedUrl.setHash(hashToSave);
        shortenedUrlRepository.save(shortenedUrl);

        String key = "shortUrl:" + hashToSave;
        redisFacade.saveToRedisCache(key, shortenedUrl);
        String staticUrlPlushHash =  staticUrl + hashToSave;
        return new ShortenedUrlDto(staticUrlPlushHash);
    }

    @Override
    public String findUrlByHash(String hash) {
        String key = "shortUrl:" + hash;
        Object cachedObject = redisFacade.checkCache(key);
        if (cachedObject instanceof ShortenedUrl shortedUrl) {
            increasePopularityIndex(hash);
            return shortedUrl.getLongUrl();
        }
        ShortenedUrl shortenedUrl = shortenedUrlRepository.findByHash(hash)
                .orElseThrow(() -> {
                    log.error("URL for the given hash {} does not exist", hash);
                    return new EntityNotFoundException(
                            String.format("URL for the given hash %s does not exist", hash)
                    );
                });
        increasePopularityIndex(hash);
        return shortenedUrl.getLongUrl();
    }

    @Override
    @Transactional
    public void deleteCreatedAYearAgo() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        List<ShortenedUrl> oldUrls = shortenedUrlRepository.findShortenedUrlsByCreatedAtBefore(oneYearAgo);
        if (!oldUrls.isEmpty()) {
            List<String> hashesToRecycle = oldUrls.stream().map(ShortenedUrl::getHash).toList();
            List<Hash> hashes = hashesToRecycle.stream()
                    .map(Hash::new)
                    .toList();
            for (int i = 0; i < hashes.size(); i += batchSize) {
                List<Hash> part = hashes.subList(i, Math.min(i + batchSize, hashes.size()));
                hashRepository.saveAll(part);
                hashRepository.flush();
            }
            shortenedUrlRepository.deleteAll(oldUrls);
            log.warn("Deleted {} old shortened URLs (recycled {} hashes)", oldUrls.size(), hashes.size());
        }
    }

    private void increasePopularityIndex(String hash) {
      redisFacade.increasePopularity(hash);
    }
}
