package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.properties.HashLocalCacheProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {
    private static final int LOCAL_CACHE_SIZE = 10;
    private static final int LOCAL_CACHE_LOW_PERCENT = 20;

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private HashLocalCacheProperties hashLocalCacheProperties;

    private HashCache hashCache;

    @BeforeEach
    void setUp() {
        lenient().when(hashLocalCacheProperties.getSize()).thenReturn(LOCAL_CACHE_SIZE);
        lenient().when(hashLocalCacheProperties.getLowPercent()).thenReturn(LOCAL_CACHE_LOW_PERCENT);

        hashCache = new HashCache(hashGenerator, hashLocalCacheProperties);
    }

    @Test
    void initTest() {
        when(hashGenerator.isBelowMinimum()).thenReturn(false);
        when(hashGenerator.getHashes(anyInt())).thenReturn(List.of("hash1", "hash2", "hash3"));

        hashCache.init();

        assertEquals("hash1", hashCache.getHash());
        assertEquals("hash2", hashCache.getHash());
        assertEquals("hash3", hashCache.getHash());
        assertNull(hashCache.getHash());
    }
}
