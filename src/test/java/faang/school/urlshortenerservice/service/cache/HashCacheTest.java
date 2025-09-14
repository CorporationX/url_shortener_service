package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.generator.HashGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {
    @Mock
    private HashGenerator generator;
    @Mock
    private HashRepository hashRepository;
    @InjectMocks
    private HashCacheImpl cache;

    @Test
    void testInit() {
        cache.init();

        verify(generator).generateBatch();
    }
}