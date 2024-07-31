package faang.school.urlshortenerservice.cache;

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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {
    @Mock
    private HashRepository hashRepository;
    @Mock
    private HashGenerator hashGenerator;
    @Mock
    private ArrayBlockingQueue<String> hashCacheQueue;
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
        //Arrange
        List<String> hashes = Arrays.asList("sdf", "sdf");
        Field field = HashCache.class.getDeclaredField("hashCacheQueue");
        field.setAccessible(true);
        ArrayBlockingQueue<String> actualHashCash = new ArrayBlockingQueue<>(10);
        field.set(hashCache, actualHashCash);
        actualHashCash.addAll(hashes);
        //Act
        String hash = hashCache.getHash();
        //Assert
        assertEquals("sdf",hash);
    }

    @Test
    void testGetHashWithException() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        // Arrange
        Field field = HashCache.class.getDeclaredField("hashCacheQueue");
        field.setAccessible(true);
        field.set(hashCache, hashCacheQueue);
        when(hashCacheQueue.take()).thenThrow(new InterruptedException());
        // Act
        String result = hashCache.getHash();
        // Assert
        assertEquals("", result);
        verify(hashCacheQueue, times(1)).take();
        assertTrue(Thread.currentThread().isInterrupted());
    }
}