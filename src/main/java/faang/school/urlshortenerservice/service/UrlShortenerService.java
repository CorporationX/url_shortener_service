package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.LocalCache;
import faang.school.urlshortenerservice.dto.UrlShortenerDto;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.ShortenedUrl;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.ShortenedUrlRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlShortenerService {

    private final LocalCache cache;
    private final ShortenedUrlRepository shortenedUrlRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final HashRepository hashRepository;
    @Value("${spring.data.redis.cashingDuration}")
    private int cachingDuration;
    @Value("${staticUrl}")
    private String staticUrl;


    @Transactional
    public String create(UrlShortenerDto urlShortenerDto) {
        String hashToSave = cache.getHash();
        String LongUrl = urlShortenerDto.getLongUrl();
        ShortenedUrl shortenedUrl = new ShortenedUrl();
        shortenedUrl.setLongUrl(LongUrl);
        shortenedUrl.setHash(hashToSave);
        shortenedUrlRepository.save(shortenedUrl);

        String key = "shortUrl:" + hashToSave;
        redisTemplate.opsForValue()
                .set(key, shortenedUrl, Duration.ofHours(cachingDuration));
        return staticUrl + hashToSave;
    }

    public String findUrlByHash(String hash) {
        String key = "shortUrl:" + hash;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof ShortenedUrl su) {
            increasePopularityIndex(hash);
            return su.getLongUrl();
        }
        ShortenedUrl shortenedUrl = shortenedUrlRepository
                .findByHash(hash)
                .orElseThrow(() -> {
                    log.error("URL for the given hash {} does not exist", hash);
                    return new EntityNotFoundException(
                            String.format("URL for the given hash %s does not exist", hash)
                    );
                });
        increasePopularityIndex(hash);
        return shortenedUrl.getLongUrl();
    }

    @Transactional
    public void deleteCreatedAYearAgo() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        List<ShortenedUrl> oldUrls = shortenedUrlRepository.findShortenedUrlsByCreatedAtBefore(oneYearAgo);
        if (!oldUrls.isEmpty()) {
            int batchSize;
            List<String> hashesToRecycle = oldUrls.stream().map(ShortenedUrl::getHash).toList();
            int total = hashesToRecycle.size();
            if (total <= 1000) {
                batchSize = 100;
            } else if (total <= 5000) {
                batchSize = 500;
            } else {
                batchSize = 1000;
            }
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
        redisTemplate.opsForZSet()
                .incrementScore("popular:urls", hash, 1);
    }
}
