package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.FreeHash;
import faang.school.urlshortenerservice.repository.FreeHashJdbcRepository;
import faang.school.urlshortenerservice.repository.FreeHashRepository;
import faang.school.urlshortenerservice.repository.RedisShortenerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CacheRefillerTransactionalTest {
    @Mock
    private FreeHashGenerator freeHashGenerator;

    @Mock
    private FreeHashRepository freeHashRepository;

    @Mock
    private RedisShortenerRepository shortenerRepository;

    @Mock
    private FreeHashJdbcRepository freeHashJdbcRepository;

    @InjectMocks
    private CacheRefillerTransactional cacheRefillerTransactional;

    private List<Long> range;
    private List<FreeHash> hashes;

    @BeforeEach
    void setUp() {
        range = List.of(0L, 1L);
        hashes = List.of(new FreeHash("hash1"), new FreeHash("hash2"));
        ReflectionTestUtils.setField(cacheRefillerTransactional, "maxCapacity", range.size());
    }

    @Test
    void refillRedisAtStart_ShouldRefillRedisFromGenerator() {
        when(freeHashGenerator.generateHashes(range)).thenReturn(hashes);
        doReturn(range).when(freeHashRepository).generateBatch(range.size());
        when(shortenerRepository.tryLockForGeneration()).thenReturn(true);

        cacheRefillerTransactional.refillRedisAtStart();

        verify(shortenerRepository).saveFreeHashesBatch(anyList());
    }

    @Test
    void refillRedisFromGenerator_ShouldPushHashesToRedis() {
        when(shortenerRepository.tryLockForGeneration()).thenReturn(true);

        cacheRefillerTransactional.refillRedisFromGenerator(range);

        verify(shortenerRepository).saveFreeHashesBatch(anyList());
    }

    @Test
    void refillRedisFromDb_ShouldPushHashesToRedisFromDb() {
        List<FreeHash> freeHashesFromDb = List.of(new FreeHash("hash1"), new FreeHash("hash2"));

        when(freeHashRepository.findAndLockFreeHashes(anyInt())).thenReturn(freeHashesFromDb);
        when(shortenerRepository.tryLockForGeneration()).thenReturn(true);

        cacheRefillerTransactional.refillRedisFromDb(10L);

        verify(shortenerRepository).saveFreeHashesBatch(anyList());
        verify(freeHashJdbcRepository).deleteByIds(anyList());
    }

    @Test
    void refillRedisFromDb_ShouldNotProceedIfLockNotAcquired() {
        when(shortenerRepository.tryLockForGeneration()).thenReturn(false);

        cacheRefillerTransactional.refillRedisFromDb(10L);

        verify(shortenerRepository, never()).saveFreeHashesBatch(anyList());
        verify(freeHashJdbcRepository, never()).deleteByIds(anyList());
    }

    @Test
    void refillRedisFromGenerator_ShouldNotProceedIfLockNotAcquired() {
        when(shortenerRepository.tryLockForGeneration()).thenReturn(false);

        cacheRefillerTransactional.refillRedisFromGenerator(List.of(1L, 2L));

        verify(shortenerRepository, never()).saveFreeHashesBatch(anyList());
    }
}