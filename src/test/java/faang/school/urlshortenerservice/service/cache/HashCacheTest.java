package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.service.HashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {

    @Mock
    private HashService hashService;

    private HashCache hashCache;

    private final int capacity = 10;
    private final float fillPercentage = 20f;

    @BeforeEach
    public void setup() {
        hashCache = new HashCache(hashService);
        ReflectionTestUtils.setField(hashCache, "capacity", capacity);
        ReflectionTestUtils.setField(hashCache, "fillPercentage", fillPercentage);

        List<String> initialHashes = Arrays.asList("hash1", "hash2", "hash3", "hash4", "hash5",
                "hash6", "hash7", "hash8", "hash9", "hash10");
        when(hashService.getHashes(capacity)).thenReturn(initialHashes);
        hashCache.init();
    }

    @Test
    public void getHash_ShouldReturnHeadOfQueue() {
        String hash = hashCache.getHash();
        assertThat(hash).isEqualTo("hash1");
    }

    @Test
    public void getHash_ShouldTriggerRefillWhenBelowThreshold() throws Exception {
        ReflectionTestUtils.setField(hashCache, "hashes",
                new ArrayBlockingQueue<>(capacity, false, Collections.singletonList("hashX")));

        List<String> newHashes = Arrays.asList("hashA", "hashB", "hashC", "hashD", "hashE",
                "hashF", "hashG", "hashH", "hashI", "hashJ");
        when(hashService.getHashes(capacity)).thenReturn(newHashes);
        when(hashService.generateBatch()).thenReturn(CompletableFuture.completedFuture(null));

        String result = hashCache.getHash();
        assertThat(result).isEqualTo("hashX");
        verify(hashService, atLeastOnce()).getHashes(capacity);
    }
}
