package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {
    @Mock
    private HashRepository hashRepository;
    @Mock
    private Base62Encoder base62Encoder;
    private int batchSize = 5;

    private HashGenerator hashGenerator;

    @BeforeEach
    public void init() {
        hashGenerator = new HashGenerator(hashRepository, base62Encoder, batchSize);
    }

    @Test
    void testSmoke() {
        List<Long> generatedNumbers = List.of(1L, 2L, 3L, 4L, 5L);
        Mockito.when(hashRepository.getUniqueNumbers(batchSize)).thenReturn(generatedNumbers);
        List<String> generatedHashes = List.of("1", "2", "3", "4", "5");
        Mockito.when(base62Encoder.encode(generatedNumbers)).thenReturn(generatedHashes);

        assertDoesNotThrow(() -> hashGenerator.generateBatch());
        Mockito.verify(hashRepository, times(1)).saveAll(any());
    }

}
