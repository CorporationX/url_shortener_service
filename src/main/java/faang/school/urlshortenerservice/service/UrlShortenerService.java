package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.FreeHash;
import faang.school.urlshortenerservice.entity.UrlMapping;
import faang.school.urlshortenerservice.enums.HashStatus;
import faang.school.urlshortenerservice.repository.RedisShortenerRepository;
import faang.school.urlshortenerservice.repository.UrlMappingRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class UrlShortenerService {
    private final UrlMappingRepository urlMappingRepository;
    private final HashPoolService hashPoolService;
    private final RedisShortenerRepository redisService;

    @Transactional
    public FreeHash generateShortUrl(String longUrl, int ttlMinutes) {
        FreeHash hash = hashPoolService.getAvailableHash();

        UrlMapping mapping = createActiveUrlMapping(longUrl, ttlMinutes, hash.getHash());
        urlMappingRepository.save(mapping);
        redisService.saveShortUrl(hash.getHash(), longUrl, ttlMinutes);
        return hash;
    }

    @Transactional(readOnly = true)
    public String resolveLongUrl(String hash) throws EntityNotFoundException, IllegalStateException {
        String longUrl = redisService.getLongUrl(hash);
        if (longUrl != null) {
            return longUrl;
        }

        UrlMapping mapping = urlMappingRepository.findByHashThrow(hash);
        checkUrlMappingStatusAndExpiredThrow(mapping);

        long minutesLeft = Duration.between(LocalDateTime.now(), mapping.getExpiredAt()).toMinutes();
        redisService.saveShortUrl(hash, mapping.getLongUrl(), minutesLeft);
        return mapping.getLongUrl();
    }

    private void checkUrlMappingStatusAndExpiredThrow(UrlMapping mapping) {
        if (mapping.getStatus() != HashStatus.ACTIVE) {
            throw new IllegalStateException("Short url is not active");
        }

        if (mapping.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Short url expired");
        }
    }

    private UrlMapping createActiveUrlMapping(String longUrl, int ttlMinutes, String hash) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredAt = now.plusMinutes(ttlMinutes);

        return new UrlMapping(hash, longUrl, now, expiredAt);
    }
}
