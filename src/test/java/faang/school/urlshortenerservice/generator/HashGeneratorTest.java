package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {
    @Mock
    private HashRepository hashRepository;
    @Spy
    private Base62Encoder encoder;
    @InjectMocks
    private HashGenerator hashGenerator;
    private int generateBatchSize = 2;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashGenerator, "generateBatchSize", generateBatchSize);
    }

    @Test
    public void testGenerateBatch() {
        List<Long> uniqueNumbers = List.of(10L, 124L);
        List<String> hashes = List.of("A", "02");
        when(hashRepository.getUniqueNumbers(generateBatchSize)).thenReturn(uniqueNumbers);

        hashGenerator.generateBatch();

        verify(hashRepository).save(hashes);
    }
}
