package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.generator.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
public class TestHashCache {
    @Mock
    private HashGenerator hashGenerator;
    @Mock
    private Queue<String> hashesCache = new ArrayDeque<>(10);
//    @Mock
//    private ExecutorService cachePool;
    @Mock
    private AtomicBoolean running = new AtomicBoolean(false);
    @InjectMocks
    private HashCache hashCache;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    int capacityTest = 0;
    int lowPercentageTest = 0;

    @BeforeEach
    public void setup() {

//        hashCache = new HashCache(hashGenerator,executorService);
        hashCache.setCapacity(10);
        hashCache.setLowFillPercentage(20);

    }

    @Test
    public void getHashLowPercentageTest() {
        hashesCache.add("hash1");
        List<String> hashes = List.of("hash1", "hash2", "hash3", "hash4", "hash5", "hash6");
        when(hashGenerator.getHashes(10)).thenReturn(hashes);
        String result = hashCache.getHash();
        assertEquals("hash1", result);
        assertEquals("hash2", hashCache.getHash());
        assertEquals("hash3", hashCache.getHash());
        assertEquals("hash4", hashCache.getHash());
    }

    @Test
    public void getHashTest() {

        hashesCache.add("hash1");
        hashesCache.add("hash2");
        hashesCache.add("hash3");
        hashesCache.add("hash4");
        hashesCache.add("hash5");
        List<String> initialHashes = Arrays.asList("hash1", "hash2");
        when(hashGenerator.getHashes(10)).thenReturn(initialHashes);

        List<String> newHashes = Arrays.asList("hash4", "hash5");
        when(hashGenerator.getHashes(8)).thenReturn(newHashes);  // ????????? 8 ????? ????? ??? ????????

        // ?????????? ?????? ????, ??? ???????? ?????????? ????
        assertEquals("hash1", hashCache.getHash());

        // ???????? ?????? ???????????? ?????? ?????????? ????
        verify(hashGenerator, times(1)).getHashes(8); // ??????? ?????????? ????

        // ????????, ??? ??? ?????????? ?????? ??????????
        assertEquals("hash2", hashCache.getHash());
        assertEquals("hash4", hashCache.getHash());
        assertEquals("hash5", hashCache.getHash());
    }
}
