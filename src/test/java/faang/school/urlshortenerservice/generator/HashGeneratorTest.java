package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.config.hash.UrlShortenerConfig;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {
    private final static int BATCH_SIZE = 3;

    private final int batchSize = BATCH_SIZE;
    private final List<Long> numbers = List.of(1L, 2L, 3L);
    private final List<String> hashes = List.of("a", "b", "c");

    @InjectMocks
    private HashGenerator hashGenerator;
    @Mock
    private HashRepository hashRepository;
    @Mock
    private Base62Encoder base62Encoder;
    @Mock
    private UrlShortenerConfig urlShortenerConfig;

    @Test
    void testSuccessfullyBatchGenerated() {
        when(urlShortenerConfig.getNumberCount()).thenReturn(batchSize);
        when(hashRepository.getUniqueNumbers(batchSize)).thenReturn(numbers);
        when(base62Encoder.encode(numbers)).thenReturn(hashes);

        hashGenerator.generateBatch();

        verify(urlShortenerConfig, times(1)).getNumberCount();
        verify(hashRepository, times(1)).getUniqueNumbers(batchSize);
        verify(base62Encoder, times(1)).encode(numbers);
        verify(hashRepository, times(1)).save(hashes);
    }

    @Test
    void testBatchGeneratedWhenEmptyHashListReturned() {
        when(urlShortenerConfig.getNumberCount()).thenReturn(batchSize);
        when(hashRepository.getUniqueNumbers(batchSize)).thenReturn(List.of());
        when(base62Encoder.encode(List.of())).thenReturn(List.of());

        hashGenerator.generateBatch();

        verify(hashRepository,times(1)).save(List.of());
    }

    @Test
    void testBatchGeneratedExceptionHandled() {
        when(urlShortenerConfig.getNumberCount()).thenReturn(batchSize);
        when(hashRepository.getUniqueNumbers(batchSize))
                .thenThrow(new RuntimeException("Get sequence error"));

        assertDoesNotThrow(hashGenerator::generateBatch);
    }
}

