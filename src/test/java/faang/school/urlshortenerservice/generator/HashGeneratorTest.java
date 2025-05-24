package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.JdbcHashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @Mock
    private JdbcHashRepository jdbcHashRepository;

    @Mock
    private Base62Encoder encoder;

    private HashGenerator hashGenerator;

    private final List<Long> numbers = List.of(1L, 2L, 3L);
    private final List<String> hashes = List.of("a", "b", "c");

    @Value("${hash.generator.batch-size}")
    private int batchSize;

    @BeforeEach
    void setUp() {
        hashGenerator = new HashGenerator(jdbcHashRepository, encoder, batchSize);
    }

    @Test
    void generateBatch_shouldFetchEncodeAndSave() {
        when(jdbcHashRepository.getNextNumbers(batchSize)).thenReturn(numbers);
        when(encoder.encode(numbers)).thenReturn(hashes);

        List<String> result = hashGenerator.generateBatch();

        verify(jdbcHashRepository).getNextNumbers(batchSize);
        verify(encoder).encode(numbers);
        verify(jdbcHashRepository).save(hashes);

        assertEquals(hashes, result);
    }
}