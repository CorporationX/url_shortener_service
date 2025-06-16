package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.SequenceRepository;
import faang.school.urlshortenerservice.service.hash.HashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test cases of HashGeneratorTest")
public class HashGeneratorTest {

    private static final int BATCH_SIZE = 100;

    @Mock
    private HashService hashService;

    @Mock
    private SequenceRepository sequenceRepository;

    @InjectMocks
    private HashGenerator hashGenerator;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(hashGenerator, "batchSize", BATCH_SIZE);
    }

    @Test
    @DisplayName("generateBatch - success")
    public void testGenerateBatchSuccess() {
        List<Long> numbers = List.of(1L, 2L, 3L);
        List<String> encodeNumbers = List.of("1", "2", "3");
        when(sequenceRepository.getUniqueNumbers(BATCH_SIZE)).thenReturn(numbers);

        hashGenerator.generateBatch();

        verify(sequenceRepository, times(1)).getUniqueNumbers(BATCH_SIZE);
        verify(hashService, times(1)).save(encodeNumbers);
    }
}
