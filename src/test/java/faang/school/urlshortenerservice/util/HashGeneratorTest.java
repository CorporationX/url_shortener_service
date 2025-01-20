package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.service.HashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @Mock
    private HashService hashService;

    @Mock
    private BaseEncoder baseEncoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    private ThreadPoolTaskExecutor shortenerTaskExecutor;
    private Long batchSize;
    private int lowThresholdPercent;
    private List<Long> numbers;
    private List<Hash> encodedHashes;

    @BeforeEach
    void setUp() {
        shortenerTaskExecutor = new ThreadPoolTaskExecutor();
        shortenerTaskExecutor.initialize();

        batchSize = 3L;
        lowThresholdPercent = 20;
        numbers = new ArrayList<>(List.of(1L, 2L, 3L));
        encodedHashes = new ArrayList<>(
                List.of(Hash.builder().hash("1").build(),
                        Hash.builder().hash("2").build(),
                        Hash.builder().hash("3").build()));
        hashGenerator = new HashGenerator(hashService, baseEncoder, shortenerTaskExecutor);
        ReflectionTestUtils.setField(hashGenerator, "batchSize", batchSize);
        ReflectionTestUtils.setField(hashGenerator, "lowThresholdPercent", lowThresholdPercent);
    }

    @Test
    void testGenerateHashBatch_WhenRefillNeeded() {
        when(hashService.getHashRepositorySize()).thenReturn(10L);
        when(hashService.getUniqueSeqNumbers(batchSize)).thenReturn(numbers);
        when(baseEncoder.encodeList(numbers)).thenReturn(encodedHashes);

        hashGenerator.asyncHashRepositoryRefill().join();

        verify(hashService, times(1)).getHashRepositorySize();
        verify(hashService, times(1)).getUniqueSeqNumbers(anyLong());
        verify(baseEncoder, times(1)).encodeList(numbers);
        verify(hashService, times(1)).saveHashes(encodedHashes);
    }

    @Test
    void testGenerateHashBatch_WhenRefillNotNeeded() {
        when(hashService.getHashRepositorySize()).thenReturn(1000L);

        hashGenerator.asyncHashRepositoryRefill().join();

        verify(hashService, times(1)).getHashRepositorySize();
        verify(hashService, never()).getUniqueSeqNumbers(anyLong());
    }
}