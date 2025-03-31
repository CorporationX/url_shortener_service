package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.FreeHash;
import faang.school.urlshortenerservice.entity.UrlMapping;
import faang.school.urlshortenerservice.repository.RedisShortenerRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashFacade hashFacade;
    private final RedisShortenerRepository redisShortenerRepository;

    @Value("${shortener.max-url-expired-seconds}")
    private int maxTtlSeconds;

    @PostConstruct
    public void init(){
        hashFacade.warmUpCache();
    }

    @Transactional
    public FreeHash generateShortUrl(String longUrl, int ttlSeconds) {
        checkTtlThrow(ttlSeconds);

        FreeHash hash = hashFacade.getAvailableHash();
        UrlMapping mapping = createActiveUrlMapping(longUrl, ttlSeconds, hash.getHash());
        urlRepository.save(mapping);
        redisShortenerRepository.saveShortUrl(hash.getHash(), longUrl, ttlSeconds);

        return hash;
    }

    @Transactional(readOnly = true)
    public String resolveLongUrl(String hash) throws EntityNotFoundException, IllegalStateException {
        String longUrl = redisShortenerRepository.getLongUrl(hash);
        if (longUrl != null) {
            return longUrl;
        }

        UrlMapping mapping = urlRepository.findByHashThrow(hash);
        checkUrlMappingStatusAndExpiredThrow(mapping);

        long minutesLeft = Duration.between(LocalDateTime.now(), mapping.getExpiredAt()).toMinutes();
        redisShortenerRepository.saveShortUrl(hash, mapping.getLongUrl(), minutesLeft);
        return mapping.getLongUrl();
    }

    private void checkUrlMappingStatusAndExpiredThrow(UrlMapping mapping) {
        if (mapping.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Short url expired");
        }
    }

    private UrlMapping createActiveUrlMapping(String longUrl, int ttl, String hash) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredAt = now.plusSeconds(ttl);

        return new UrlMapping(hash, longUrl, now, expiredAt);
    }

    private void checkTtlThrow(int ttlSeconds) {
        if (ttlSeconds > maxTtlSeconds) {
            throw new IllegalArgumentException();
        }
    }
}
