package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashJdbcRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    @Mock
    private HashJdbcRepository hashJdbcRepository;

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private ExecutorService hashCacheExecutor;

    @InjectMocks
    private HashCache hashCache;

    @Test
    void getHash_triggersRefill_whenCacheBelowThreshold() {
        when(hashJdbcRepository.getHashBatch())
            .thenReturn(List.of("h1", "h2"));

        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(hashCacheExecutor).submit(any(Runnable.class));

        String hash = hashCache.getHash();

        assertNotNull(hash);
        verify(hashJdbcRepository).getHashBatch();
        verify(hashGenerator).generateBatch();
    }
}
