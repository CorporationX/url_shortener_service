package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.generator.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocalCacheServiceTest {

    @Mock
    private HashGenerator hashGenerator;

    @InjectMocks
    private LocalCacheService localCacheService;

    private final int capacity = 5;
    private final List<String> seedHashes = List.of("h1", "h2", "h3", "h4", "h5");
    private final int lowPercent = 20;
    private final int maxWaitForValueMS = 500;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(localCacheService, "capacity", capacity);
        ReflectionTestUtils.setField(localCacheService, "lowPercent", lowPercent);
        ReflectionTestUtils.setField(localCacheService, "maxWaitForValueMS", maxWaitForValueMS);
    }

    @Test
    void init_seedsCacheWithHashes() {
        when(hashGenerator.getHashes(anyLong()))
                .thenReturn(CompletableFuture.completedFuture(seedHashes))
                .thenReturn(CompletableFuture.completedFuture(List.of("h11")));

        localCacheService.init();

        verify(hashGenerator).getHashes(capacity);
        List<String> cached = drainQueue();
        assertThat(cached).containsExactlyElementsOf(seedHashes);
    }

    @Test
    void getHash_returnsFromCache_whenAvailable() {
        when(hashGenerator.getHashes(capacity))
                .thenReturn(CompletableFuture.completedFuture(seedHashes));

        localCacheService.init();

        String result = localCacheService.getHash();

        assertThat(result).isEqualTo(seedHashes.get(0));
    }

    @Test
    void getHash_throwsWhenCacheEmptyAfterTimeout() {
        when(hashGenerator.getHashes(capacity))
                .thenReturn(CompletableFuture.completedFuture(List.of()));
        localCacheService.init();

        assertThatThrownBy(() -> localCacheService.getHash())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void getHash_triggersRefillWhenCacheLow() {
        List<String> initialCache = List.of("h1");
        List<String> refilledCache = List.of("h2", "h3", "h4", "h5");

        when(hashGenerator.getHashes(anyLong()))
                .thenReturn(CompletableFuture.completedFuture(initialCache)) // seed
                .thenReturn(CompletableFuture.completedFuture(refilledCache)); // refill

        localCacheService.init();

        String lastInCache = localCacheService.getHash();
        String fromRefilledCache = localCacheService.getHash();

        assertThat(lastInCache).isEqualTo(initialCache.get(0));
        assertThat(fromRefilledCache).isIn(refilledCache);

        verify(hashGenerator, times(2)).getHashes(anyLong());
    }

    private List<String> drainQueue() {
        List<String> cached = new ArrayList<>();
        for (int i = 0; i < capacity; i++) {
            cached.add(localCacheService.getHash());
        }
        return cached;
    }
}
