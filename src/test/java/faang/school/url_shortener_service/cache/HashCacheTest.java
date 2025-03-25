package faang.school.url_shortener_service.cache;

import faang.school.url_shortener_service.generator.AsynchronousHashGenerator;
import faang.school.url_shortener_service.generator.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    @InjectMocks
    private HashCache hashCache;

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private AsynchronousHashGenerator asynchronousHashGenerator;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashCache, "capacity", 5);
        ReflectionTestUtils.setField(hashCache, "filledPercentage", 50);
        ReflectionTestUtils.setField(hashCache, "hashBatchSize", 2);
    }

    @Test
    void shouldOfferToCacheOrStoreRest_ifCacheIsFull() {
        ReflectionTestUtils.setField(hashCache, "capacity", 2);
        ReflectionTestUtils.setField(hashCache, "hashes", new ArrayBlockingQueue<>(2));
        hashCache.offerToCacheOrStoreRest(List.of("x", "y"));

        // Fill it manually
        hashCache.offerToCacheOrStoreRest(List.of("z"));
        verify(hashGenerator).saveHashesToDb(List.of("z"));
    }

    @Test
    void shouldAddAllToCache_ifSpaceAvailable() {
        ReflectionTestUtils.setField(hashCache, "capacity", 5);
        ReflectionTestUtils.setField(hashCache, "hashes", new ArrayBlockingQueue<>(5));
        List<String> hashes = List.of("a", "b");

        hashCache.offerToCacheOrStoreRest(hashes);

        verify(hashGenerator, never()).saveHashesToDb(anyList());
    }
}