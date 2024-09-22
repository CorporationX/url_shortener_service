package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.generator.HashGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {
    @Mock
    private HashGenerator hashGenerator;
    @InjectMocks
    private HashCache hashCache;

    @BeforeEach
    public void setUp() {
        Queue<String> mockedQueue = new ArrayBlockingQueue<>(5);
        List<String> mockedList = new ArrayList<>();
        mockedQueue.add("hash1");
        mockedQueue.add("hash2");
        mockedQueue.add("hash3");
        mockedQueue.add("hash4");
        mockedQueue.add("hash5");
        mockedList.add("hash1");
        mockedList.add("hash2");
        mockedList.add("hash3");
        mockedList.add("hash4");
        mockedList.add("hash5");
        ReflectionTestUtils.setField(hashCache, "hashes", mockedQueue);
        ReflectionTestUtils.setField(hashCache, "lowPercentage", 20);
        ReflectionTestUtils.setField(hashCache, "hashes", mockedQueue);
        ReflectionTestUtils.setField(hashCache, "capacity", 5);
        Mockito.when(hashGenerator.getHashes(5)).thenReturn(mockedList);
        hashCache.fill();
    }

    @SuppressWarnings("unchecked")
    private <T> T getAllHash() {
        return (T) ReflectionTestUtils.getField(hashCache, "hashes");
    }

    @Test
    public void fillTest() {
        Queue<String> actualHashes = getAllHash();
        Assertions.assertNotNull(actualHashes);
        Assertions.assertEquals(5,actualHashes.size());
    }

    @Test
    public void testGetAllHash() {
        String actualHash = hashCache.getHash();
        Assertions.assertEquals("hash1", actualHash);
        Mockito.verify(hashGenerator, Mockito.times(0)).getHashesAsync(5);
    }

    @Test
    public void testGetAllHashWithLowStorage() {
        List<String> newHashes = new ArrayList<>();
        newHashes.add("hash6");
        newHashes.add("hash7");
        newHashes.add("hash8");
        newHashes.add("hash9");
        Mockito.when(hashGenerator.getHashesAsync(5)).thenReturn(CompletableFuture.completedFuture(newHashes));
        hashCache.getHash();
        hashCache.getHash();
        hashCache.getHash();
        hashCache.getHash();
        hashCache.getHash();
        Mockito.verify(hashGenerator, Mockito.times(1)).getHashesAsync(5);
        Queue<String> actualHashes = getAllHash();
        Assertions.assertEquals(4, actualHashes.size());
    }

}
