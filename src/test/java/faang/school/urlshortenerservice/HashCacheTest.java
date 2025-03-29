package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashCache;
import faang.school.urlshortenerservice.service.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashGenerator hashGenerator;

    @InjectMocks
    private HashCache hashCache;

    @BeforeEach
    void setUp() {
        hashCache = new HashCache(hashRepository, hashGenerator);
        lenient().when(hashRepository.getHashBatch(anyInt())).thenReturn(List.of(new Hash("abc123")));
    }

    @Test
    void testGetHash_Success() {
        hashCache.init();
        String hash = hashCache.getHash();

        assertEquals("abc123", hash);
        verify(hashRepository).getHashBatch(anyInt());
    }

    @Test
    void testGetHash_TriggersGenerationWhenCacheBelowThreshold() {
        hashCache.init();
        when(hashRepository.getHashBatch(anyInt())).thenReturn(List.of());
        String hash = hashCache.getHash();
        assertEquals("abc123", hash);

        hashCache.getHash();
        verify(hashGenerator, atLeastOnce()).generateBatch();
    }

    @Test
    void testGetHash_ReturnsHashWithoutGenerationWhenCacheFull() {
        hashCache.init();
        String hash = hashCache.getHash();
        assertEquals("abc123", hash);

        verify(hashGenerator, never()).generateBatch();
    }
}
