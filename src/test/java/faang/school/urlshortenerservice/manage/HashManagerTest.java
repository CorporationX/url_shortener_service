package faang.school.urlshortenerservice.manage;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.exception.HashCacheIsEmptyException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HashManagerTest {

    @Mock
    private HashCache hashCache;

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private HashManager hashManager;

    @Test
    @DisplayName("Should return hash from cache")
    public void testGetHash_CacheHit() {
        String expectedHash = "abc123";
        when(hashCache.getHash()).thenReturn(expectedHash);

        String actualHash = hashManager.getHash();

        assertEquals(expectedHash, actualHash);
        verify(hashCache).getHash();
    }

    @Test
    @DisplayName("Should throw exception when hash cache is empty")
    public void testGetHash_CacheEmpty() {
        when(hashCache.getHash()).thenReturn(null);

        HashCacheIsEmptyException exception = assertThrows(HashCacheIsEmptyException.class, () -> {
            hashManager.getHash();
        });

        assertEquals("List with hashes is empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should generate new hashes when cache is less than required")
    public void testGetHash_GenerateNewHashes() {
        when(hashCache.cacheSizeLessThanRequired()).thenReturn(true);
        when(hashCache.getFreeCapacityInCollection()).thenReturn(10);
        when(hashGenerator.generateBatchAsync(10)).thenReturn(CompletableFuture.completedFuture(List.of("newHash1", "newHash2")));
        when(hashCache.getHash()).thenReturn("newHash1");

        String actualHash = hashManager.getHash();

        assertEquals("newHash1", actualHash);
        verify(hashGenerator).generateBatchAsync(10);
    }

    @Test
    @DisplayName("Should save expired hashes")
    public void testSaveHashes() {
        List<String> expiredHashes = List.of("hash1", "hash2");

        hashManager.saveHashes(expiredHashes);

        verify(hashRepository).save(expiredHashes);
    }

    @Test
    @DisplayName("Should fill hash cache on post construct")
    public void testFillHash() {
        when(hashGenerator.generateBatch(anyInt())).thenReturn(List.of("hash1", "hash2"));
        when(hashCache.getFreeCapacityInCollection()).thenReturn(2);

        hashManager.fillHash();

        verify(hashCache).fillingCache(List.of("hash1", "hash2"));
    }
}