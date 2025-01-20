package faang.school.urlshortenerservice.local_cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class HashCacheTest {

    @MockBean
    private HashGenerator hashGenerator;

    @Autowired
    private HashCache hashCache;

    @Test
    void testGetHashReturnsCachedHash() {
        when(hashGenerator.getHashes(any(Integer.class))).thenReturn(List.of("hash1", "hash2"));
        hashCache.init();

        String result = hashCache.getHash();

        assertEquals("hash1", result);
    }

    @Test
    void testInitInitializesCache() {
        when(hashGenerator.getHashes(any(Integer.class))).thenReturn(List.of("hash1", "hash2"));

        hashCache.init();

        assertFalse(hashCache.getCache().isEmpty());
    }

    @Test
    void testInitDoesNotPopulateCacheWhenHashesEmpty() {
        when(hashGenerator.getHashes(any(Integer.class))).thenReturn(Collections.emptyList());

        hashCache.init();

        assertTrue(hashCache.getCache().isEmpty());
    }

    @Test
    void testAsyncCallsAreTriggeredAsExpected() throws Exception {
        List<String> mockHashes = List.of("hash1", "hash2", "hash3");
        when(hashGenerator.getHashes(any(Integer.class)))
                .thenReturn(mockHashes);

        hashCache.init();

        CompletableFuture<List<String>> future = hashCache.getHashesAsync(3);
        List<String> result = future.get();

        assertEquals(mockHashes, result);
    }

    @Test
    void testGetHashesAsyncReturnsEmptyListWhenGeneratorReturnsEmpty() throws Exception {
        when(hashGenerator.getHashes(any(Integer.class))).thenReturn(Collections.emptyList());

        CompletableFuture<List<String>> future = hashCache.getHashesAsync(5);
        List<String> result = future.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetHashesAsyncHandlesExceptionGracefully() {
        when(hashGenerator.getHashes(any(Integer.class))).thenThrow(new RuntimeException("Simulated exception"));

        CompletableFuture<List<String>> future = hashCache.getHashesAsync(5);

        assertThrows(Exception.class, future::get);
    }

    @Test
    void testGetHashesAsyncSupportsConcurrentCalls() throws Exception {
        List<String> mockHashes1 = List.of("hash1", "hash2");
        List<String> mockHashes2 = List.of("hash3", "hash4");
        when(hashGenerator.getHashes(any(Integer.class))).thenReturn(mockHashes1, mockHashes2);

        CompletableFuture<List<String>> future1 = hashCache.getHashesAsync(2);
        CompletableFuture<List<String>> future2 = hashCache.getHashesAsync(2);

        List<String> result1 = future1.get();
        List<String> result2 = future2.get();

        assertEquals(mockHashes1, result1);
        assertEquals(mockHashes2, result2);
    }

    @Test
    void testNoDeadlockWhenConcurrentRequestsAreMade() {
        when(hashGenerator.getHashes(any(Integer.class))).thenReturn(Collections.emptyList());

        hashCache.init();

        String result = hashCache.getHash();

        assertNull(result);
    }
}