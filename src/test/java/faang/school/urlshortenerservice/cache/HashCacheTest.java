package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Queue<String> hashQueue;

    @Mock
    private HashGenerator hashGenerator;

    @InjectMocks
    private HashCache hashCache;

    @BeforeEach
    public void beforeEach() {
        ReflectionTestUtils.setField(hashCache, "hashCacheSize", 5);
        ReflectionTestUtils.setField(hashCache, "minCachePercentage", 0.8);

        hashQueue = new ArrayBlockingQueue<>(10);
        hashQueue.addAll(List.of("1", "2", "3", "4", "5"));

        ReflectionTestUtils.setField(hashCache, "hashQueue", hashQueue);
    }

    @Test
    public void getHashTestSuccessCase() {
        assertEquals("1", hashCache.getHash());
        assertEquals("2", hashCache.getHash());

    }

    @Test
    public void getHashTestLessThanRequired() {
        Mockito.doNothing().when(hashGenerator).generateBatch();
        Mockito.when(hashRepository.getHashBatch(anyInt())).thenReturn(List.of("6", "7", "8"));

        assertEquals("1", hashCache.getHash());
        assertEquals("2", hashCache.getHash());
        assertEquals("3", hashCache.getHash());

        await().atMost(10, SECONDS).until(() -> hashQueue.size() == 5);
    }

    @Test
    public void getHashTestNoElements() {
        assertEquals("1", hashCache.getHash());
        assertEquals("2", hashCache.getHash());
        assertEquals("3", hashCache.getHash());
        assertEquals("4", hashCache.getHash());
        assertEquals("5", hashCache.getHash());
        assertThrows(NoSuchElementException.class, () -> hashCache.getHash());

    }
}
