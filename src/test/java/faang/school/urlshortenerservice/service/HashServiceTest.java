package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.FreeHash;
import faang.school.urlshortenerservice.repository.FreeHashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashServiceTest {

    @Mock
    private FreeHashRepository freeHashRepository;

    @Mock
    private FreeHashGenerator freeHashGenerator;

    @Mock
    private ApplicationContext applicationContext;

    @InjectMocks
    private HashService hashService;

    private final FreeHash freeHash = new FreeHash("abc123");

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashService, "maxDbCapacity", 5L);
        ReflectionTestUtils.setField(hashService, "maxCacheCapacity", 10L);
        ReflectionTestUtils.setField(hashService, "refillThresholdPercent", 50);
    }

    @Test
    void warmUpCache_shouldRefillCacheFromDb() {
        when(freeHashRepository.count()).thenReturn(5L);
        when(freeHashRepository.deleteAndReturnFreeHashes(10)).thenReturn(List.of(freeHash));

        hashService.warmUpCache();

        verify(freeHashRepository).deleteAndReturnFreeHashes(10);
        verifyNoInteractions(freeHashGenerator);
    }

    @Test
    void refill_shouldTriggerDbRefillIfNotEnoughHashesInDb() {
        when(freeHashRepository.count()).thenReturn(2L);
        when(freeHashRepository.deleteAndReturnFreeHashes(3)).thenReturn(List.of(freeHash));
        when(applicationContext.getBean(HashService.class)).thenReturn(hashService);

        hashService.refill(3);

        verify(applicationContext).getBean(HashService.class);
        verify(freeHashGenerator).refillDb(6L);
    }

    @Test
    void refill_shouldNotTriggerDbRefillIfEnoughInDb() {
        when(freeHashRepository.count()).thenReturn(20L);
        when(freeHashRepository.deleteAndReturnFreeHashes(5)).thenReturn(List.of(freeHash));

        hashService.refill(5);

        verify(freeHashRepository).deleteAndReturnFreeHashes(5);
        verify(freeHashGenerator, never()).refillDb(anyLong());
    }
}