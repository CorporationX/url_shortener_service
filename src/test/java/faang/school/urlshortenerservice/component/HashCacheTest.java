package faang.school.urlshortenerservice.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class HashCacheTest {

    @Mock
    private HashGenerator hashGenerator;

    @InjectMocks
    private HashCache hashCache;

    private static final int TEST_CAPACITY = 10;
    private static final List<String> TEST_HASHES = List.of("hash1", "hash2", "hash3");

    @BeforeEach
    void setUp() {
        hashCache = new HashCache(hashGenerator);
        ReflectionTestUtils.setField(hashCache, "capacity", 10);
        ReflectionTestUtils.setField(hashCache, "fillPercent", 50);
    }

    @Test
    public void init_ShouldFillCacheWithHashes_WhenCalled() {

        when(hashGenerator.getHashes(anyLong())).thenReturn(TEST_HASHES);
        hashCache.init();

        Queue<String> hashes = hashCache.getHashes();

        assertEquals(TEST_HASHES.size(), hashes.size());
        assertTrue(hashes.containsAll(TEST_HASHES));
        verify(hashGenerator).getHashes((long) TEST_CAPACITY);
    }

    @Test
    void init_ShouldThrowException_WhenHashGeneratorFails() {
        when(hashGenerator.getHashes(anyLong())).thenThrow(new RuntimeException("Generation error"));
        assertThrows(RuntimeException.class, () -> hashCache.init(),
                "Должно выбрасываться исключение при ошибке генерации хешей");
    }

    @Test
    public void testPositiveGetHash() {

        when(hashGenerator.getHashes(anyLong())).thenReturn(TEST_HASHES);
        when(hashGenerator.getHashesAsync(anyLong()))
                .thenReturn(CompletableFuture.completedFuture(TEST_HASHES));

        hashCache.init();

        String hash = hashCache.getHash();
        hashCache.getHash();
        hashCache.getHash();
        hashCache.getHash();
        hashCache.getHash();
        Queue<String> hashes = hashCache.getHashes();
        assertEquals(7, hashes.size());
    }
}

