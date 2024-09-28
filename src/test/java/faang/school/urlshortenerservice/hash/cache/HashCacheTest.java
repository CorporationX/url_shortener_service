package faang.school.urlshortenerservice.hash.cache;

import faang.school.urlshortenerservice.exception.UniqueHashNotFoundException;
import faang.school.urlshortenerservice.hash.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {
    @Mock
    private HashRepository hashRepository;
    @Mock
    private HashGenerator hashGenerator;
    @Mock
    private ThreadPoolTaskExecutor taskExecutor;
    @InjectMocks
    private HashCache hashCache;
    private AtomicBoolean generateHashesRunning;
    private ConcurrentLinkedQueue<String> cache;

    @BeforeEach
    void setUp() {
        cache = new ConcurrentLinkedQueue<>();
        generateHashesRunning = new AtomicBoolean(false);
        ReflectionTestUtils.setField(hashCache, "minimalAmountInPercentage", 20);
        ReflectionTestUtils.setField(hashCache, "maxSize", 5);
        ReflectionTestUtils.setField(hashCache, "generateHashesRunning", generateHashesRunning);
        ReflectionTestUtils.setField(hashCache, "cache", cache);
    }

    @Test
    void init() {
    }

    @Test
    void getNextUniqueHash() {
        doAnswer(invocationOnMock -> invocationOnMock);

        assertThrows(UniqueHashNotFoundException.class, () -> hashCache.getNextUniqueHash());

        verify(taskExecutor).submit(any(Runnable.class));
        verify(hashRepository).getHashBatch();

    }

}