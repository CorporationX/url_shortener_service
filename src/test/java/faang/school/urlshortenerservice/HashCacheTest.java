package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.config.HashCacheProperties;
import faang.school.urlshortenerservice.repository.FreeHashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    @Mock
    private FreeHashRepository freeHashRepository;
    @Mock
    private HashGenerator hashGenerator;
    @Mock
    private ExecutorService executorService;
    @Mock
    private HashCacheProperties properties;

    private HashCache hashCache;

    @BeforeEach
    void setUp() {
        when(properties.getMaxSize()).thenReturn(10);
        when(properties.getRefillThreshold()).thenReturn(0.5);

        // «Синхронно» выполняем submit-ы
        when(executorService.submit(any(Runnable.class))).thenAnswer(invocation -> {
            Runnable r = invocation.getArgument(0);
            r.run();
            return mock(Future.class);
        });

        hashCache = new HashCache(
                freeHashRepository,
                hashGenerator,
                executorService,
                properties
        );
    }

    @Test
    void shouldReturnHashFromCache_whenCacheIsNotEmpty() {
        // given
        ConcurrentLinkedDeque<String> cache =
                (ConcurrentLinkedDeque<String>) ReflectionTestUtils.getField(hashCache, "cache");
        assert cache != null;
        cache.add("cachedHash");

        when(freeHashRepository.fetchFreeHashes(anyInt()))
                .thenReturn(List.of("n1", "n2", "n3")); // для refill()

        // when
        String actual = hashCache.getHash();

        // then
        assertThat(actual).isEqualTo("cachedHash");
        verify(freeHashRepository, never()).fetchFreeHash(); // синхронно хеш из БД не брали
        // refill() должен был стартовать
        verify(executorService, atLeastOnce()).submit(any(Runnable.class));
    }

    @Test
    void shouldFetchHashFromDb_whenCacheIsEmpty() {
        // given
        when(freeHashRepository.fetchFreeHash()).thenReturn("dbHash");
        when(freeHashRepository.fetchFreeHashes(anyInt())).thenReturn(List.of()); // refill после fetchFreeHash

        // when
        String actual = hashCache.getHash();

        // then
        assertThat(actual).isEqualTo("dbHash");
        verify(freeHashRepository).fetchFreeHash();
    }

    @Test
    void shouldTriggerAsyncRefill_whenBelowThreshold() {
        // given
        ConcurrentLinkedDeque<String> cache =
                (ConcurrentLinkedDeque<String>) ReflectionTestUtils.getField(hashCache, "cache");
        assert cache != null;
        cache.addAll(List.of("h1", "h2", "h3")); // size = 3 (< 50 % после poll)

        when(freeHashRepository.fetchFreeHashes(anyInt()))
                .thenReturn(List.of("n1", "n2", "n3", "n4", "n5", "n6", "n7"));

        // when
        hashCache.getHash(); // заберём h1, останется 2 (порог пройден)

        // then
        verify(executorService, atLeastOnce()).submit(any(Runnable.class));
        verify(hashGenerator).generateBatch();

        // итоговый размер: capacity-1 (первый hash мы забрали)
        assertThat(cache).hasSize(9);
    }
}