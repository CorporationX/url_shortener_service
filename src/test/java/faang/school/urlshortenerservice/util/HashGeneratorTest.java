package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;


@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {
    @Mock
    private HashRepository hashRepository;
    @Mock
    private Base62Encoder base62Encoder;
    @InjectMocks
    private HashGenerator hashGenerator;
    private final int batchSize = 3;

    @Test
    void generateBatchTest() {
        List<Long> numbers = List.of(1L, 2L, 3L);
        List<String> hashes = List.of("a", "b", "c");

        Mockito.when(hashRepository.getUniqueNumbers(batchSize)).thenReturn(numbers);
        Mockito.when(base62Encoder.encode(numbers)).thenReturn(hashes);
        ReflectionTestUtils.setField(hashGenerator, "batchSize", batchSize);

        hashGenerator.generateBatch();

        Mockito.verify(hashRepository, Mockito.times(1)).getUniqueNumbers(batchSize);
        Mockito.verify(base62Encoder, Mockito.times(1)).encode(numbers);
        Mockito.verify(hashRepository, Mockito.times(1)).save(hashes);
    }
}