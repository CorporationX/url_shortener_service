package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.config.properties.hash.HashCacheProperties;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.service.generator.HashBatchGenerator;
import faang.school.urlshortenerservice.service.generator.HashGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private HashBatchGenerator hashBatchGenerator;

    @Mock
    private HashRepository hashRepository;

    private final Executor directExecutor = Runnable::run;

    private HashCacheImpl cache;

    @Captor
    private ArgumentCaptor<Integer> intCaptor;

    @Test
    @DisplayName("init fills entirely from DB when enough hashes are available")
    public void initFillsFromDb() {
        HashCacheProperties props = new HashCacheProperties(5, 20);
        cache = new HashCacheImpl(hashGenerator, hashBatchGenerator, hashRepository, props, directExecutor);

        when(hashRepository.getHashBatch(5)).thenReturn(List.of("h1","h2","h3","h4","h5"));

        cache.init();

        verify(hashRepository, times(1)).getHashBatch(5);
        verify(hashBatchGenerator, never()).generateBatch();
        assertNotNull(cache.getHash());
    }

    @Test
    @DisplayName("init generates when DB returns fewer hashes than capacity")
    public void initGeneratesWhenDbInsufficient() {
        HashCacheProperties props = new HashCacheProperties(5, 20);
        cache = new HashCacheImpl(hashGenerator, hashBatchGenerator, hashRepository, props, directExecutor);

        when(hashRepository.getHashBatch(5)).thenReturn(List.of("h1","h2"));
        when(hashRepository.getHashBatch(3)).thenReturn(List.of("h3","h4","h5"));

        cache.init();

        verify(hashRepository, times(1)).getHashBatch(5);
        verify(hashBatchGenerator, times(1)).generateBatch();
        verify(hashRepository, times(1)).getHashBatch(3);
    }

    @Test
    @DisplayName("getHash does not trigger refill when above threshold")
    public void getHashNoRefillAboveLimit() {
        HashCacheProperties props = new HashCacheProperties(5, 20);
        cache = new HashCacheImpl(hashGenerator, hashBatchGenerator, hashRepository, props, directExecutor);

        when(hashRepository.getHashBatch(5)).thenReturn(List.of("h1","h2","h3","h4","h5"));
        cache.init();

        reset(hashRepository, hashBatchGenerator, hashGenerator);

        String h = cache.getHash();
        assertNotNull(h);

        verifyNoInteractions(hashRepository, hashBatchGenerator, hashGenerator);
    }

    @Test
    @DisplayName("getHash triggers single refill below threshold and fills back to capacity")
    public void getHashTriggersRefillOnceBelowLimit() {
        HashCacheProperties props = new HashCacheProperties(4, 75);
        cache = new HashCacheImpl(hashGenerator, hashBatchGenerator, hashRepository, props, directExecutor);

        when(hashRepository.getHashBatch(4)).thenReturn(List.of("h1"));
        when(hashRepository.getHashBatch(3)).thenReturn(List.of());
        cache.init();

        reset(hashRepository, hashBatchGenerator, hashGenerator);

        when(hashRepository.getHashBatch(3)).thenReturn(List.of("h2", "h3"));
        when(hashRepository.getHashBatch(1)).thenReturn(List.of("h4"));
        when(hashGenerator.generateBatchAsync())
                .thenReturn(CompletableFuture.completedFuture(List.of("g1", "g2")));

        String got = cache.getHash();
        assertEquals("h1", got);

        verify(hashRepository, times(2)).getHashBatch(intCaptor.capture());
        List<Integer> calls = intCaptor.getAllValues();
        assertEquals(3, calls.get(0));
        assertEquals(1, calls.get(1));

        verify(hashGenerator, times(1)).generateBatchAsync();
        verifyNoInteractions(hashBatchGenerator);
    }
}
