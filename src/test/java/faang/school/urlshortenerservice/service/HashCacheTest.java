package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {
    @Mock
    private HashRepository hashRepository;
    @Mock
    private HashGenerator hashGenerator;
    @Mock
    private ExecutorService executorService;

    @InjectMocks
    private HashCache hashCache;
    @Mock
    private Queue<Hash> hashQueue;
    @Mock
    private AtomicBoolean isFilling;

    @BeforeEach
    void setUp() {
        hashQueue = new ConcurrentLinkedQueue<>();
        isFilling = new AtomicBoolean(false);
        hashCache = new HashCache(executorService, hashRepository, hashGenerator);
    }

    @Test
    void testGetHash_ShouldNotGenerateNewHashesAndAddToQueue_WhenQueueIsAboveThreshold() {
        hashQueue.add(Hash.builder().hash("abc123").build());

        Hash result = hashCache.getHash();

        assertNull(result);
        assertEquals(1, hashQueue.size());
        assertFalse(isFilling.get());
        verifyNoInteractions(hashRepository);
    }
}