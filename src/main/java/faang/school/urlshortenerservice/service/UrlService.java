package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlService {

    @Value("${redis.ttl.seconds:3600}")
    private final long redisTtlSeconds;

    private final RedisTemplate<String, String> redisTemplate;
    private final HashCache hashCache;
    private final UrlRepository urlRepository;

    @Transactional
    public String createShortUrl(String longUrl) {
        String hash = hashCache.getHash().getHash();
        Url url = new Url(hash, longUrl);
        urlRepository.save(url);
        redisTemplate.opsForValue().set(hash, longUrl, redisTtlSeconds, TimeUnit.SECONDS);
        log.debug("Short URL created: {} -> {}", hash, longUrl);
        return hash;
    }

    public String getLongUrl(String hash) {
        String url = redisTemplate.opsForValue().get(hash);
        if (url != null) {
            return url;
        }

        Optional<Url> urlOptional = urlRepository.findByHash(hash);
        if (urlOptional.isPresent()) {
            url = urlOptional.get().getUrl();
            redisTemplate.opsForValue().set(hash, url, redisTtlSeconds, TimeUnit.SECONDS);
            return url;
        }

        throw new EntityNotFoundException("URL not found for hash: " + hash);
    }
}
