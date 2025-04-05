package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.service.generator.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {
    private final static int TEST_CAPACITY = 10;
    private final static int TEST_MAX_RANGE = 100;
    private final static long TEST_PERCENT_TO_FILL = 30;

    @Mock
    private HashGenerator hashGenerator;

    @InjectMocks
    private HashCache hashCache;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashCache, "capacity", TEST_CAPACITY);
        ReflectionTestUtils.setField(hashCache, "maxRange", TEST_MAX_RANGE);
        ReflectionTestUtils.setField(hashCache, "percentToFill", TEST_PERCENT_TO_FILL);

        when(hashGenerator.getHashes(TEST_CAPACITY))
                .thenReturn(generateTestHashes(TEST_CAPACITY));

        hashCache.initCache();
    }

    @Test
    void getHash_shouldTriggerRefillWhenBelowThreshold() {
        when(hashGenerator.getHashesAsync(TEST_MAX_RANGE))
                .thenReturn(CompletableFuture.completedFuture(generateTestHashes(TEST_MAX_RANGE)));

        IntStream.range(0, 8).forEach(i -> hashCache.getHash());

        Hash result = hashCache.getHash();
        assertNotNull(result);

        verify(hashGenerator, times(1)).getHashesAsync(TEST_MAX_RANGE);
    }

    @Test
    void getHash_shouldNotTriggerRefillWhenAboveThreshold() {
        IntStream.range(0, 6).forEach(i -> hashCache.getHash());

        verify(hashGenerator, never()).getHashesAsync(anyInt());
    }

    @Test
    void getHash_shouldNotTriggerMultipleRefills() {
        CompletableFuture<List<Hash>> future = new CompletableFuture<>();
        when(hashGenerator.getHashesAsync(TEST_MAX_RANGE)).thenReturn(future);

        IntStream.range(0, 8).forEach(i -> hashCache.getHash());

        IntStream.range(0, 3).forEach(i -> hashCache.getHash());

        verify(hashGenerator, times(1)).getHashesAsync(TEST_MAX_RANGE);
    }

    private List<Hash> generateTestHashes(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> new Hash("hash" + i))
                .toList();
    }
}