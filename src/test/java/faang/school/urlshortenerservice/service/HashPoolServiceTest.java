package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.HashPoolStatus;
import faang.school.urlshortenerservice.entity.FreeHash;
import faang.school.urlshortenerservice.repository.FreeHashJdbcRepository;
import faang.school.urlshortenerservice.repository.RedisShortenerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashPoolServiceTest {

    @Mock
    private RedisShortenerRepository redisShortenerRepository;

    @Mock
    private CacheRefillerTransactional cacheRefillerTransactional;

    @Mock
    private FreeHashJdbcRepository freeHashJdbcRepository;

    @InjectMocks
    private HashPoolService hashPoolService;


    @Test
    void warmUpCache_ShouldRefillRedisIfEmpty() {
        when(redisShortenerRepository.getFreeHashListSize()).thenReturn(0L);

        hashPoolService.warmUpCache();

        verify(cacheRefillerTransactional).refillRedisAtStart();
    }

    @Test
    void warmUpCache_ShouldNotRefillRedisIfNotEmpty() {
        when(redisShortenerRepository.getFreeHashListSize()).thenReturn(10L);

        hashPoolService.warmUpCache();

        verify(cacheRefillerTransactional, never()).refillRedisAtStart();
    }
    @Test
    void getAvailableHash_ShouldReturnHashFromRedis() {
        FreeHash freeHash = new FreeHash("abc123");
        when(redisShortenerRepository.getFreeHashWithStatus()).thenReturn(new HashPoolStatus("abc123", 5L));

        FreeHash result = hashPoolService.getAvailableHash();

        assertEquals(freeHash.getHash(), result.getHash());
        verify(freeHashJdbcRepository, never()).deleteOneFreeHashAndReturnHash();
    }

    @Test
    void getAvailableHash_ShouldReturnHashFromDatabaseIfRedisIsEmpty() {
        FreeHash dbHash = new FreeHash("xyz789");
        when(redisShortenerRepository.getFreeHashWithStatus()).thenReturn(new HashPoolStatus(null, 0L));
        when(freeHashJdbcRepository.deleteOneFreeHashAndReturnHash()).thenReturn(dbHash);

        FreeHash result = hashPoolService.getAvailableHash();

        assertEquals(dbHash.getHash(), result.getHash());
        verify(freeHashJdbcRepository).deleteOneFreeHashAndReturnHash();
    }
}