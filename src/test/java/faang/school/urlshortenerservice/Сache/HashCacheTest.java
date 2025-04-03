package faang.school.urlshortenerservice.Сache;

import faang.school.urlshortenerservice.Cache.HashCache;
import faang.school.urlshortenerservice.HashGenerator.HashGenerator;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {
    Queue<String> hashes;
    @Mock
    private HashRepository hashRepository;
    @Mock
    private HashGenerator hashGenerator;
    @InjectMocks
    private HashCache hashCache;

    @BeforeEach
    public void beforeEach() {
        ReflectionTestUtils.setField(hashCache, "cacheSize", 10);
        ReflectionTestUtils.setField(hashCache, "threshold", 0.2);

        hashes = new ArrayBlockingQueue<>(10);
        ReflectionTestUtils.setField(hashCache, "hashQueue", hashes);

        ThreadPoolTaskExecutor executorService = new ThreadPoolTaskExecutor();
        executorService.initialize();
        ReflectionTestUtils.setField(hashCache, "executorService", executorService);
    }

    @Test
    public void getHashFromFullCash() {
        List<String> hashesList = List.of("1", "2", "3", "4", "5");
        hashes.addAll(hashesList);

        String result = hashCache.getHash();
        assertEquals(result, hashesList.get(0));
    }

    @Test
    public void getHashFromCacheLessThreshold() {

        hashes.add("1");
        List<String> hashesName = List.of("2", "3", "4", "5");
        List<Hash> hashesEntity = hashesName.stream().map(Hash::new).toList();
        Mockito.when(hashRepository.getHashBatch(anyInt())).thenReturn(hashesEntity);

        String result = hashCache.getHash();

        verify(hashGenerator, atLeastOnce()).generateHash();
        verify(hashRepository, atLeastOnce()).getHashBatch(anyInt());

        assertEquals(hashes.size(), hashesName.size());
        assertEquals(result, "1");
    }

    @Test
    public void getHashEmptyCache() {
        Mockito.when(hashRepository.getHashBatch(anyInt())).thenReturn(List.of());
        Exception exception = assertThrows(NoSuchElementException.class, () -> hashCache.getHash());
        assertEquals(exception.getMessage(), "Отсутствуют свободные хэши");
    }
}
