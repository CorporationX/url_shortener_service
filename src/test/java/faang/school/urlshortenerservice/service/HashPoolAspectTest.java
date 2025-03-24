package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.HashPoolStatus;
import faang.school.urlshortenerservice.repository.FreeHashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashPoolAspectTest {

    @Mock
    private CacheRefillerTransactional cacheRefillerTransactional;

    @Mock
    private FreeHashRepository freeHashRepository;

    @Mock
    private CacheRefillerAsync cacheRefillerAsync;

    @InjectMocks
    private HashPoolAspect hashPoolAspect;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashPoolAspect, "maxCapacity", 100);
        ReflectionTestUtils.setField(hashPoolAspect, "refillThresholdPercent", 50);
    }

    @Test
    void triggerRefillIfNeeded_ShouldTriggerRefillWhenThresholdIsNotMet() {
        HashPoolStatus emptyStatus = new HashPoolStatus("emptyHash", 40L);
        when(freeHashRepository.count()).thenReturn(40L);

        hashPoolAspect.triggerRefillIfNeeded(emptyStatus);

        verify(cacheRefillerAsync).refillRedisFromGenerator(anyList());
        verify(cacheRefillerTransactional, never()).refillRedisFromDb(anyLong());
    }

    @Test
    void triggerRefillIfNeeded_ShouldNotTriggerRefillWhenThresholdIsMet() {
        HashPoolStatus status = new HashPoolStatus("hash", 80L);
        when(freeHashRepository.count()).thenReturn(40L);

        hashPoolAspect.triggerRefillIfNeeded(status);

        verify(cacheRefillerAsync, never()).refillRedisFromGenerator(anyList());
        verify(cacheRefillerTransactional, never()).refillRedisFromDb(anyLong());
    }

    @Test
    void triggerRefillIfNeeded_RedisIsEmpty() {
        HashPoolStatus status = new HashPoolStatus("hash", null);
        when(freeHashRepository.count()).thenReturn(30L);

        hashPoolAspect.triggerRefillIfNeeded(status);

        verify(cacheRefillerAsync).refillRedisFromGenerator(anyList());
        verify(cacheRefillerTransactional, never()).refillRedisFromDb(anyLong());
    }

    @Test
    void triggerRefillIfNeeded_DbFull() {
        HashPoolStatus status = new HashPoolStatus("hash", null);
        when(freeHashRepository.count()).thenReturn(200L);

        hashPoolAspect.triggerRefillIfNeeded(status);

        verify(cacheRefillerAsync, never()).refillRedisFromGenerator(anyList());
        verify(cacheRefillerTransactional).refillRedisFromDb(anyLong());
    }

}