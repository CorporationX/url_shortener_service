package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ArrayBlockingQueue;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {
    @Mock
    private HashGenerator hashGenerator;
    @Mock
    private HashRepository hashRepository;
    @InjectMocks
    private HashCache hashCache;
    private final int capacity = 10;
    private final ArrayBlockingQueue<Hash> queue = new ArrayBlockingQueue<>(capacity);
    private final Hash hash1 = new Hash("Hash1");
    private final Hash hash2 = new Hash("Hash2");

    @BeforeEach
    public void init() {
        int lowLimitPercent = 20;
        hashCache.setQueue(queue);
        hashCache.setCapacity(capacity);
        hashCache.setLowLimitPercent(lowLimitPercent);


    }

    @Test
    public void testGetHashFill() {
        queue.add(hash1);
        Assertions.assertEquals(hash1, hashCache.getHash());
        Mockito.verify(hashRepository, Mockito.times(1)).getHashBatch(capacity - queue.size() - 1);
        Mockito.verify(hashGenerator, Mockito.times(1)).generateBatch();
    }

    @Test
    public void testGetHash() {
        queue.add(hash1);
        queue.add(hash2);
        Assertions.assertEquals(hash1, hashCache.getHash());
        Mockito.verify(hashRepository, Mockito.times(0)).getHashBatch(Mockito.anyInt());
        Mockito.verify(hashGenerator, Mockito.times(0)).generateBatch();
    }
}
