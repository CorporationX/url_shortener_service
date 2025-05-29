package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.properties.CacheProperties;
import faang.school.urlshortenerservice.repository.JdbcHashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashServiceTest {

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private HashCache hashCache;

    @Mock
    private JdbcHashRepository jdbcHashRepository;

    @Mock
    private CacheProperties cacheProperties;

    @InjectMocks
    private HashService hashService;

    private final List<String> testHashes = List.of("a", "b");
    private final String testHash = "ab";

    @Test
    void initCache_shouldLoadFromDbIfAvailable() {
        when(cacheProperties.getFillSize()).thenReturn(2);
        when(jdbcHashRepository.getAndRemoveBatch(2)).thenReturn(testHashes);

        hashService.initCache();

        verify(hashCache).addAll(testHashes);
    }

    @Test
    void initCache_shouldWarnIfDbEmpty() {
        when(cacheProperties.getFillSize()).thenReturn(2);
        when(jdbcHashRepository.getAndRemoveBatch(2)).thenReturn(List.of());

        hashService.initCache();

        verify(hashCache, never()).addAll(anyList());
    }

    @Test
    void getNextHash_shouldReturnFromCache() {
        when(hashCache.poll()).thenReturn(testHash);
        when(hashCache.size()).thenReturn(200); // не триггерим refill
        when(cacheProperties.getMinSize()).thenReturn(100);

        String result = hashService.getNextHash();

        assertEquals(testHash, result);
    }

    @Test
    void getNextHash_shouldUseFallbackIfCacheEmpty() {
        when(hashCache.poll()).thenReturn(null).thenReturn(testHash);
        when(cacheProperties.getFillSize()).thenReturn(2);
        when(jdbcHashRepository.getAndRemoveBatch(2)).thenReturn(testHashes);
        when(hashCache.size()).thenReturn(200); // refill не нужен
        when(cacheProperties.getMinSize()).thenReturn(100);

        String result = hashService.getNextHash();

        verify(hashCache).addAll(testHashes);
        assertEquals(testHash, result);
    }

    @Test
    void getNextHash_shouldThrowIfNoFallbackAvailable() {
        when(hashCache.poll()).thenReturn(null);
        when(jdbcHashRepository.getAndRemoveBatch(2)).thenReturn(List.of());
        when(cacheProperties.getFillSize()).thenReturn(2);
        when(hashCache.size()).thenReturn(50);
        when(cacheProperties.getMinSize()).thenReturn(100);

        assertThrows(IllegalStateException.class, () -> hashService.getNextHash());
    }

    @Test
    void maybeTriggerRefill_shouldGenerateMoreHashesAsync() {
        when(hashCache.size()).thenReturn(50);
        when(cacheProperties.getMinSize()).thenReturn(100);
        when(hashGenerator.generateBatch()).thenReturn(testHashes);
        when(hashCache.poll()).thenReturn(testHash);

        String result = hashService.getNextHash();

        assertEquals(testHash, result);

        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() ->
                verify(hashCache).addAll(testHashes));
    }
}
