package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.properties.CacheProperties;
import faang.school.urlshortenerservice.repository.JdbcHashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void getNextHash_shouldReturnFromCache() {
        when(hashCache.poll()).thenReturn(testHash);
        String result = hashService.getNextHash();
        assertEquals(testHash, result);
    }

    @Test
    void getNextHash_shouldUseFallbackIfCacheEmpty() {
        when(hashCache.poll()).thenReturn(null).thenReturn(testHash);
        when(cacheProperties.getFillSize()).thenReturn(2);
        when(jdbcHashRepository.getAndRemoveBatch(2)).thenReturn(testHashes);

        String result = hashService.getNextHash();

        verify(hashCache).addAll(testHashes);
        assertEquals(testHash, result);
    }

    @Test
    void generateMoreHashes_shouldGenerateAndAddToCache() {
        when(hashGenerator.generateBatch()).thenReturn(testHashes);

        hashService.generateMoreHashes();

        await()
                .atMost(3, SECONDS)
                .untilAsserted(() -> verify(hashCache).addAll(testHashes));
    }
}
