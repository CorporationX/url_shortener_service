package faang.school.urlshortenerservice.hash;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {

    private HashCache hashCache;
    private int maxCacheSize = 5;
    private double thresholdFractionSize = 0.2;

    @Mock
    private ThreadPoolTaskExecutor executor;

    @Mock
    private HashFiller hashFiller;

    @Mock
    private BlockingQueue<String> queueHash;

    @BeforeEach
    public void setup() {
        hashCache = new HashCache(
            maxCacheSize,
            thresholdFractionSize,
            executor,
            hashFiller,
            queueHash
        );
    }

    @Test
    public void testGetHash_CheckAndFillHashCache() throws InterruptedException {
        // Arrange
        when(queueHash.poll(3, TimeUnit.SECONDS)).thenReturn("123");

        // Act
        String receivedHash = hashCache.getHash();

        // Assert
        assertEquals("123", receivedHash);
    }
}
