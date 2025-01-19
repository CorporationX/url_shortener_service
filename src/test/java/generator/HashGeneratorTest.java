package generator;

import faang.school.urlshortenerservice.config.HashGeneratorProperties;
import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @Mock
    private HashGeneratorProperties hashGeneratorProperties;

    private HashGenerator hashGenerator;

    @BeforeEach
    void setup() {
        hashGenerator = new HashGenerator(hashRepository, base62Encoder, hashGeneratorProperties);
    }

    @Test
    void testGenerateBatch() throws InterruptedException {
        // arrange
        int batchSize = 5;
        when(hashGeneratorProperties.getBatchSize()).thenReturn(batchSize);

        List<Long> uniqueNumbers = Arrays.asList(10L, 11L, 12L, 13L, 14L);
        when(hashRepository.getUniqueNumbers(batchSize)).thenReturn(uniqueNumbers);

        List<String> encodedHashes = Arrays.asList("a", "b", "c", "d", "e");
        when(base62Encoder.encode(uniqueNumbers)).thenReturn(encodedHashes);

        // act
        hashGenerator.generateBatch();

        Thread.sleep(500);

        // assert
        verify(hashRepository, times(1)).getUniqueNumbers(batchSize);
        verify(base62Encoder, times(1)).encode(uniqueNumbers);
        verify(hashRepository, times(1)).save(encodedHashes);

        verifyNoMoreInteractions(hashRepository, base62Encoder, hashGeneratorProperties);
    }
}
