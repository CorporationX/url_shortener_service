package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {
    @Mock
    private HashGenerator hashGenerator;
    @Mock
    private HashRepository hashRepository;

    private HashCache hashCache;


    //    @Test
    @RepeatedTest(1000)
    void testGetHashWhenQueueDoesNotNeedUpdate() throws InterruptedException {
        Mockito.when(hashRepository.getHashBatch(anyInt())).thenReturn(List.of("hash1", "hash2"));
        hashCache = new HashCache(hashGenerator, hashRepository, 2, 4, 0.3);

        String result = hashCache.getHash();

        assertEquals("hash1", result);
        Mockito.verify(hashRepository, times(1)).getHashBatch(anyInt());
        Mockito.verify(hashGenerator, times(1)).generateBatch();
    }

    //    @Test
    @RepeatedTest(1000)
    void testGetHashWhenQueueDoesNeedUpdate() throws InterruptedException {
        Mockito.when(hashRepository.getHashBatch(anyInt()))
                .thenReturn(List.of("hash1"))
                .thenReturn(List.of("hash2", "hash3", "hash4"));
        hashCache = new HashCache(hashGenerator, hashRepository, 2, 4, 0.3);

        String result = hashCache.getHash();
        String result2 = hashCache.getHash();

        assertEquals("hash1", result);
        assertEquals("hash2", result2);
        Mockito.verify(hashRepository, times(2)).getHashBatch(anyInt());
        Mockito.verify(hashGenerator, times(2)).generateBatch();
    }
}
