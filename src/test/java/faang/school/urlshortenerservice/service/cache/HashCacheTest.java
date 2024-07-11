package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.service.hash.HashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {

    @Mock
    private HashService hashService;

    @InjectMocks
    private HashCache hashCache;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashCache, "cacheSize", 10);
        ReflectionTestUtils.setField(hashCache, "fillingPercent", 20);
        when(hashService.generateHashes()).thenReturn(CompletableFuture.completedFuture(null));
        hashCache.init();
    }

    @Test
    void testInit() {
        List<String> mockHashes = List.of("hash1", "hash2", "hash3");
        when(hashService.getHashes(anyInt())).thenReturn(mockHashes);

        hashCache.init();

        Queue<String> hashes = getPrivateField(hashCache);
        assert hashes != null;
        assertEquals(3, hashes.size());
    }

    @SuppressWarnings("unchecked")
    private <T> T getPrivateField(Object target) {
        return (T) ReflectionTestUtils.getField(target, "hashes");
    }

    @Test
    void testGetHash() {
        List<String> mockHashes = List.of("hash1", "hash2", "hash3");
        when(hashService.getHashes(anyInt())).thenReturn(mockHashes);

        hashCache.init();

        String hash = hashCache.getHash();
        assertNotNull(hash);

        Queue<String> hashes = getPrivateField(hashCache);
        assert hashes != null;
        assertEquals(2, hashes.size());
    }

    @Test
    void testCheckAndRefillCacheIfNeeded() {
        List<String> mockHashes = List.of("hash1", "hash2", "hash3");
        when(hashService.getHashes(anyInt())).thenReturn(mockHashes);
        when(hashService.getHashesAsync(anyInt())).thenReturn(CompletableFuture.completedFuture(mockHashes));

        hashCache.init();

        hashCache.getHash();
        hashCache.getHash();
        hashCache.getHash();

        verify(hashService, times(1)).getHashesAsync(anyInt());
    }
}
