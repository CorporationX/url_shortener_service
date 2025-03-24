package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.HashPoolStatus;
import faang.school.urlshortenerservice.entity.FreeHash;
import faang.school.urlshortenerservice.repository.FreeHashJdbcRepository;
import faang.school.urlshortenerservice.repository.RedisShortenerRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HashPoolService {

    private final RedisShortenerRepository redisService;
    private final CacheRefillerTransactional cacheRefillerTransactional;
    private final FreeHashJdbcRepository freeHashJdbcRepository;

    @PostConstruct
    public void warmUpCache() {
        long redisSize = redisService.getFreeHashListSize();

        if (redisSize == 0) {
            cacheRefillerTransactional.refillRedisAtStart();
        }
    }

    public FreeHash getAvailableHash() {
        HashPoolStatus status = redisService.getFreeHashWithStatus();
        return status.hash() != null
                ? new FreeHash(status.hash())
                : freeHashJdbcRepository.deleteOneFreeHashAndReturnHash();
    }
}
