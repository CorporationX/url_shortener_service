package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashCacheServiceImplTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashGeneratorService hashGeneratorService;

    @Mock
    private ExecutorService executorService;

    @InjectMocks
    private HashCacheServiceImpl hashCacheService;

    private int fetchHashesSize;
    private Queue<String> hashes;
    private String hash;

    @BeforeEach
    void setUp() {
        fetchHashesSize = 100;
        hashes = new ConcurrentLinkedDeque<>();
        hash = "hash1";

        AtomicBoolean isReplenishing = new AtomicBoolean(false);

        ReflectionTestUtils.setField(hashCacheService, "hashes", hashes);
        ReflectionTestUtils.setField(hashCacheService, "isReplenishing", isReplenishing);
        ReflectionTestUtils.setField(hashCacheService, "fetchHashesSize", fetchHashesSize);
    }

    @Test
    public void testGetHashWhenCacheHasEnoughHashes() {
        hashes.add(hash);

        String result = hashCacheService.getHash();

        assertEquals(hash, result);
    }

    @Test
    public void testGetHashWhenCacheHasNotEnoughHashesAndIsReplenishing() {
        when(hashRepository.getHash()).thenReturn(Optional.of(hash));

        String result = hashCacheService.getHash();

        assertEquals(hash, result);
        verify(hashRepository).getHash();
    }

    @Test
    public void testGetHashWhenNoHashesAvailableAndRepositoryReturnsEmpty() {
        when(hashRepository.getHash()).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> hashCacheService.getHash());

        assertEquals("Free hash not found!", exception.getMessage());
    }

    @Test
    public void testFetchFreeHashesShouldReplenishCache() throws InterruptedException {
        List<String> fetchedHashes = Arrays.asList("hash2", "hash3");
        when(hashRepository.getHashes(fetchHashesSize)).thenReturn(fetchedHashes);
        CountDownLatch latch = new CountDownLatch(1);
        hashes.add(hash);

        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            new Thread(() -> {
                task.run();
                latch.countDown();
            }).start();
            return null;
        }).when(executorService).execute(any(Runnable.class));

        hashCacheService.getHash();

        latch.await();

        assertEquals(2, hashes.size());
        assertTrue(hashes.contains("hash2"));
        assertTrue(hashes.contains("hash3"));
        verify(executorService).execute(any(Runnable.class));
    }

    @Test
    public void testAddHash() {
        List<Hash> entities = List.of(new Hash("hash1"), new Hash("hash2"), new Hash("hash3"));
        List<String> hashes = Arrays.asList("hash1", "hash2", "hash3");

        hashCacheService.addHash(hashes);

        verify(hashRepository).saveAll(entities);
    }
}
