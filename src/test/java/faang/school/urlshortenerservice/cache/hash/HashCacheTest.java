package faang.school.urlshortenerservice.cache.hash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.utils.hash.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private ExecutorService executorService;

    @Mock
    private Hash hash1;

    @Mock
    private Hash hash2;

    @InjectMocks
    private HashCache hashCache;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashCache, "initialCacheSize", 100);

        when(hashRepository.getHashBatch()).thenReturn(List.of(hash1, hash2));
        when(hash1.getHash()).thenReturn("hash1");
        when(hash2.getHash()).thenReturn("hash2");

    }

    @Test
    @DisplayName("Should initialize hash cache with generated and fetched hashes")
    void whenInitializeCacheThenShouldPopulateCache() {
        hashCache.initializeCache();

        verify(hashGenerator).generateBatch();
        verify(hashRepository).getHashBatch();
    }

    @Test
    @DisplayName("Should return hash from the cache")
    void whenGetHashThenShouldReturnHash() {
        hashCache.initializeCache();

        String hash = hashCache.getHash();

        verify(hashRepository).getHashBatch();
        verify(hashGenerator).generateBatch();
        assertEquals("hash1", hash);
    }

    @Test
    @DisplayName("Should refill hash cache when threshold is reached")
    void whenRefillHashesThenShouldPopulateCache() {
        ReflectionTestUtils.setField(hashCache, "initialCacheSize", 10);
        ReflectionTestUtils.setField(hashCache, "hashes", new LinkedBlockingQueue<>(5));

        hashCache.initializeCache();
        hashCache.getHash();

        verify(executorService, times(2)).execute(any(Runnable.class));
    }
}

