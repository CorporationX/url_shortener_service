package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.component.Base62Encoder;
import faang.school.urlshortenerservice.component.HashGenerator;
import faang.school.urlshortenerservice.config.app.HashCacheConfig;
import faang.school.urlshortenerservice.config.app.HashGeneratorConfig;
import faang.school.urlshortenerservice.repository.interfaces.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    @Mock
    private HashCacheConfig config;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private ExecutorService hashCacheExecutor;

    @Mock
    private HashGeneratorConfig hashGeneratorConfig;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashCache hashCache;

    @BeforeEach
    void setUp() {
        doAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        }).when(hashCacheExecutor).execute(any(Runnable.class));

        doAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        }).when(hashCacheExecutor).submit(any(Runnable.class));

        when(config.getMaxSize()).thenReturn(10);
        when(config.getInitialDbSize()).thenReturn(20);
        when(config.getRefillThreshold()).thenReturn(20);
        when(hashGeneratorConfig.getBatchSize()).thenReturn(5);

        doAnswer(invocation -> {
            List<String> hashes = Arrays.asList("gen_hash1", "gen_hash2", "gen_hash3", "gen_hash4", "gen_hash5");
            hashRepository.save(hashes);
            return null;
        }).when(hashGenerator).generateBatch();
    }

    @Test
    void testInitPopulatesCacheAndGetHashWorks() {
        when(hashRepository.getHashBatch())
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(Arrays.asList("hash1", "hash2", "hash3"));

        CompletableFuture<Void> populateDbFuture = hashCache.populateDatabaseAsync();
        populateDbFuture.join();

        CompletableFuture<Void> fillCacheFuture = hashCache.fillCacheAsync();
        fillCacheFuture.join();

        verify(hashGenerator, times(4)).generateBatch();

        String hash = hashCache.getHash();
        assertNotNull(hash, "Should return a hash after initialization");
        assertTrue(hash.matches("hash\\d+"), "Hash should match expected pattern");

        verify(hashRepository, atLeast(3)).getHashBatch();

        verify(hashRepository, times(4)).save(anyList());
    }
}
