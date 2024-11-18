package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashCacheImplTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private ExecutorService executorService;

    @InjectMocks
    private HashCacheImpl hashCache;

    private int fetchHashesSize;
    private Queue<String> hashes;
    private AtomicBoolean isReplenishing;
    private String hash;

    @BeforeEach
    void setUp() {
        fetchHashesSize = 100;
        hashes = new ConcurrentLinkedDeque<>();
        isReplenishing = new AtomicBoolean(false);
        hash = "hash1";

        ReflectionTestUtils.setField(hashCache, "hashes", hashes);
        ReflectionTestUtils.setField(hashCache, "isReplenishing", isReplenishing);
        ReflectionTestUtils.setField(hashCache, "fetchHashesSize", fetchHashesSize);
    }

    @Test
    public void testGetHashWhenCacheHasEnoughHashes() {
        hashes.add(hash);

        String result = hashCache.getHash();

        assertEquals(hash, result);
    }

    @Test
    public void testGetHashWhenCacheHasNotEnoughHashesAndIsReplenishing() {
        when(hashRepository.getHash()).thenReturn(Optional.of(hash));

        String result = hashCache.getHash();

        assertEquals(hash, result);
        verify(hashRepository).getHash();
    }

    @Test
    public void testGetHashWhenNoHashesAvailableAndRepositoryReturnsEmpty() {
        when(hashRepository.getHash()).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> hashCache.getHash());

        assertEquals("Free hash not found!", exception.getMessage());
    }
}