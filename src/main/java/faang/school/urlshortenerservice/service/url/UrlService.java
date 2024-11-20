package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.config.cache.CacheProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final CacheProperties cacheProperties;
    private final RedisTemplate<String, Url> redisTemplate;

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

        Url url = Optional.ofNullable(redisTemplate.opsForValue().get(hash))
                .orElseGet(() -> urlRepository.findUrlByHash(hash)
                        .orElseThrow(() -> new EntityNotFoundException("long url for: " + hash + " - does not exist")));

        log.info("finish getLongUrl with longUrl: {}", url.getUrl());
        return url.getUrl();
    }
}
