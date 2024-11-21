package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.executor.ExecutorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {

    @Mock
    private ExecutorService executorService;

    private HashCache hashCache;


    @BeforeEach
    public void setUp() {
        List<String> hashes = List.of("100000", "200000", "300000", "400000", "500000");
        when(executorService.fillCache()).thenReturn(CompletableFuture.completedFuture(hashes));
        hashCache = new HashCache(executorService, 0.2, 10);
    }

    @Test
    public void getHashWithOutFillingCache() {
        String result = hashCache.getHash();
        assertEquals("100000", result);
    }

    @Test
    public void getHashWithFillingCache() {
        hashCache.getHash();
        hashCache.getHash();
        hashCache.getHash();
        hashCache.getHash();

        verify(executorService, times(2)).fillCache();
    }
}
