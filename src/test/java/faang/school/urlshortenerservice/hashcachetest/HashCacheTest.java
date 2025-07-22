package faang.school.urlshortenerservice.hashcachetest;

import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.generator.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {

    @Mock
    private HashGenerator hashGenerator;

    @InjectMocks
    private HashCache hashCache;

    private final int MAX_SIZE = 5;


    @BeforeEach
    void commonStubbing() {
        when(hashGenerator.getHashes(MAX_SIZE))
                .thenReturn(List.of("a", "b", "c", "d", "e"));

        when(hashGenerator.getHashesAsync(anyInt()))
                .thenAnswer(inv -> {
                    int n = inv.getArgument(0, Integer.class);
                    var batch = IntStream.range(0, n)
                            .mapToObj(i -> "X" + i)
                            .collect(Collectors.toList());
                    return CompletableFuture.completedFuture(batch);
                });

        hashCache.setMaxSize(MAX_SIZE);
    }

    @Test
    void initialFill_withZeroThreshold_refillsOnEmpty() {
        hashCache.setRefillThresholdPercent(0.0);
        hashCache.initializeCache();

        for (int i = 0; i < MAX_SIZE; i++) {
            assertTrue(hashCache.getHash().isPresent(), "Should have element #" + i);
        }

        Optional<String> sixth = hashCache.getHash();
        assertTrue(sixth.isPresent(), "After empty it should refill immediately");
        assertEquals("X0", sixth.get());

        verify(hashGenerator, times(1)).getHashesAsync(MAX_SIZE);
    }

    @Test
    void refillWhenBelowThreshold_triggersAsyncRefill() {
        hashCache.setRefillThresholdPercent(0.2);
        hashCache.initializeCache();

        for (int i = 0; i < 4; i++) {
            assertTrue(hashCache.getHash().isPresent(), "Pre-refill pull #" + i);
        }
        assertTrue(hashCache.getHash().isPresent(), "Fifth pull should still be present");
        verify(hashGenerator, times(1)).getHashesAsync(MAX_SIZE - 1);
    }
}
