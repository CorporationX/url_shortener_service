package faang.school.urlshortenerservice.service;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {
    @Mock
    private HashRepository hashRepository;

    @Spy
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    private int batchSize = 100;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(hashGenerator, "batchSize", 100);
        ReflectionTestUtils.setField(hashGenerator, "minPercentHashes", 20.0);
    }

    @Test
    public void testGetHashes() {
        List<Long> numbers = List.of(1L, 2L, 3L, 4L, 5L);
        List<String> hashes = List.of("1a", "1b", "1c", "1d", "1e");
        when(hashRepository.getHashesSize()).thenReturn(10L);
        when(hashRepository.getUniqueNumbers(batchSize)).thenReturn(numbers);
        when(base62Encoder.encode(numbers)).thenReturn(hashes);

        List<String> newHashes = hashGenerator.getHashes(5);

        assertEquals(5, newHashes.size());
    }
}