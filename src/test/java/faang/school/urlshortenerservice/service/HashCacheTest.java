package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {
    @Mock
    private HashService hashService;
    @InjectMocks
    private HashCache hashCache;

    @BeforeEach
    void setUp() {
        setField(hashCache, "cacheSize", 5);
        setField(hashCache, "cacheFreeRation", 0.4);
    }

    @Test
    void givenFreshInstance_whenInit_thenCacheIsFilled() {
        List<String> hashes = List.of("aaa", "bbb", "ccc", "ddd", "eee");
        when(hashService.getHashes(5)).thenReturn(hashes);

        hashCache.init();

        String hash = hashCache.getHash();
        assertTrue(hashes.contains(hash));
        verify(hashService, times(1)).getHashes(5);
    }

    @Test
    void givenCacheBelowThreshold_whenGetHash_thenFillsCache() {
        List<String> initialHashes = List.of("aaa", "bbb");
        List<String> refillHashes = List.of("ccc", "ddd", "eee");

        when(hashService.getHashes(5)).thenReturn(initialHashes);
        when(hashService.getHashes(4)).thenReturn(refillHashes);

        hashCache.init();
        hashCache.getHash();

        String nextHash = hashCache.getHash();

        assertNotNull(nextHash);
        verify(hashService).getHashes(4);
    }
}
