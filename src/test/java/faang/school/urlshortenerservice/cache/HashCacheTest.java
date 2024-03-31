package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.config.threadpool.ThreadPoolConfig;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {
    @Mock
    private HashRepository hashRepository;
    @Mock
    private HashGenerator hashGenerator;
    @Mock
    private ThreadPoolConfig threadPoolConfig;
    @InjectMocks
    private HashCache hashCache;


    @Test
    void testInit() {
        int countOfHashes = 0;
        ReflectionTestUtils.setField(hashCache, "cacheSize", 10);
        List<String> hashes = Arrays.asList("sdf", "sdf", "sdf", "sdf", "sdf", "sdf", "sdf", "sdf", "sdf", "sdf");
        ArrayBlockingQueue<String> hashCash = new ArrayBlockingQueue<>(10);
        hashCash.addAll(hashes);

        when(hashRepository.count()).thenReturn(countOfHashes);
        when(hashRepository.getHashBatch(10)).thenReturn(hashes);

        hashCache.init();

        verify(hashRepository).count();
        verifyNoInteractions(hashGenerator);
        verify(hashRepository).getHashBatch(10);
        assertEquals(hashes, new ArrayList<>(hashCash));
    }

    @Test
    void testInitCallsHashGeneratorMethod() {
        ReflectionTestUtils.setField(hashCache, "cacheSize", 10);
        List<String> hashes = List.of();
        when(hashRepository.getHashBatch(10)).thenReturn(hashes);
        hashCache.init();
        verify(hashGenerator).generateBatch(10);
    }

    @Test
    void testGetHash() throws NoSuchFieldException, IllegalAccessException {
        List<String> hashes = Arrays.asList("sdf", "sdf", "sdf", "sdf", "sdf", "sdf", "sdf", "sdf", "sdf", "sdf");
        List<String> expectedHashCash = Arrays.asList("sdf", "sdf", "sdf", "sdf", "sdf", "sdf", "sdf", "sdf", "sdf");
        Field field = HashCache.class.getDeclaredField("hashCash");
        field.setAccessible(true);
        ArrayBlockingQueue<String> actualHashCash = new ArrayBlockingQueue<>(10);
        field.set(hashCache, actualHashCash);
        actualHashCash.addAll(hashes);

        hashCache.getHash();

        assertEquals(expectedHashCash, new ArrayList<>(actualHashCash));
    }

    @Test
    void testGetHashWithException() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        ArrayBlockingQueue<String> mockQueue = mock(ArrayBlockingQueue.class);

        Field field = HashCache.class.getDeclaredField("hashCash");
        field.setAccessible(true);
        field.set(hashCache, mockQueue);

        when(mockQueue.take()).thenThrow(InterruptedException.class);

        assertThrows(RuntimeException.class, () -> hashCache.getHash());
    }
}