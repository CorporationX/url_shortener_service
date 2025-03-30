package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.FreeHash;
import faang.school.urlshortenerservice.repository.FreeHashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashServiceTest {

    @Mock
    private FreeHashRepository freeHashRepository;

    @Mock
    private FreeHashGenerator freeHashGenerator;

    @InjectMocks
    private HashService hashService;

    private FreeHash freeHash;

    @BeforeEach
    void setUp() {
        freeHash = new FreeHash("abc123");
        ReflectionTestUtils.setField(hashService, "maxCapacity", 10L);
        ReflectionTestUtils.setField(hashService, "refillThresholdPercent", 50);
    }

    @Test
    void warmUpCache_shouldFillCacheAtStartup() {
        when(freeHashRepository.count()).thenReturn(0L);
        when(freeHashGenerator.generateHashes(anyList())).thenReturn(Arrays.asList(freeHash));

        hashService.warmUpCache();

        verify(freeHashRepository).count();
        verify(freeHashGenerator).generateHashes(anyList());
    }

    @Test
    void refill_shouldAddHashesFromDbWhenAvailable() {
        List<FreeHash> dbHashes = Arrays.asList(freeHash);
        when(freeHashRepository.count()).thenReturn(1L);
        when(freeHashRepository.findAndLockFreeHashes(1)).thenReturn(dbHashes);

        hashService.refill(1);

        verify(freeHashRepository).deleteAllByIdInBatch(anyList());
        verify(freeHashRepository).findAndLockFreeHashes(1);
    }

    @Test
    void refill_shouldGenerateNewHashesWhenNotEnoughInDb() {
        when(freeHashRepository.count()).thenReturn(0L);
        when(freeHashGenerator.generateHashes(anyList())).thenReturn(Arrays.asList(freeHash));

        hashService.refill(1);

        verify(freeHashGenerator).generateHashes(anyList());
    }
}