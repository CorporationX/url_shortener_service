package faang.school.urlshortenerservice.cache.hash;

import faang.school.urlshortenerservice.config.cache.CacheProperties;
import faang.school.urlshortenerservice.service.hash.HashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    private static final int CACHE_CAPACITY = 10;
    private static final int CACHE_MIN_LIMIT_CAPACITY = 20;

    private static final String HASH_ONE = "HASH1";
    private static final String HASH_TWO = "HASH2";
    private static final String HASH_THREE = "HASH3";
    private static final String HASH_FOUR = "HASH4";

    @Mock
    private CacheProperties cacheProperties;

    @Mock
    private HashService hashService;

    private HashCache hashCache;

    @Nested
    class InitialClassEmpty {

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            when(cacheProperties.getCapacity()).thenReturn(CACHE_CAPACITY);
            when(cacheProperties.getMinLimitCapacity()).thenReturn(CACHE_MIN_LIMIT_CAPACITY);

            hashCache = new HashCache(cacheProperties, hashService);
        }

        @Test
        void whenHashesNotEnoughThenFillNewHashesAndReturn() {
            when(hashService.getHashesAsync())
                    .thenReturn(CompletableFuture.completedFuture(List.of(HASH_ONE)));

            assertEquals(HASH_ONE, hashCache.getHash());

            verify(hashService)
                    .getHashesAsync();
        }
    }

    @Nested
    class InitialNotEmpty {
        @BeforeEach
        void init() {
            MockitoAnnotations.openMocks(this);
            when(cacheProperties.getCapacity()).thenReturn(CACHE_CAPACITY);
            when(cacheProperties.getMinLimitCapacity()).thenReturn(CACHE_MIN_LIMIT_CAPACITY);

            when(hashService.getHashes()).thenReturn(List.of(HASH_ONE, HASH_TWO, HASH_THREE, HASH_FOUR));
            hashCache = new HashCache(cacheProperties, hashService);

        }

        @Test
        void whenHashesStartEnoughThenReturnHash() {
            assertEquals(HASH_ONE, hashCache.getHash());

            verify(hashService, times(0))
                    .getHashesAsync();
        }
    }


}