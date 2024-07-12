package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepositoryJdbc;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    @Mock
    private HashRepositoryJdbc hashRepositoryJdbc;
    @Mock
    private HashGenerator hashGenerator;

    @InjectMocks
    private HashCache hashCache;

    @Test
    public void getHashTest() {
        ReflectionTestUtils.setField(hashCache, "cacheSize", 5);
        ReflectionTestUtils.setField(hashCache, "cacheSizePercent", 20);

        hashCache.loadHashQueue();

        Mockito.verify(hashGenerator, Mockito.timeout(100)).generateBatch();
    }

}