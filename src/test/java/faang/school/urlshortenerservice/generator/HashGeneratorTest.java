package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
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
public class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;
    @Mock
    private Base62Encoder base62Encoder;
    @InjectMocks
    private HashGenerator hashGenerator;

    private final List<Long> UNIQUE_NUMBERS = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
    private final List<String> HASHES = List.of("qw", "de", "rt", "yt", "kg", "gk", "iy", "lk", "oi", "bn");
    private final int UNIQUE_NUMBER_RANGE = 10;

    @Test
    void testGenerateBatch() {
        ReflectionTestUtils.setField(hashGenerator, "uniqueNumberRange", UNIQUE_NUMBER_RANGE);
        Mockito.when(hashRepository.getUniqueNumbers(UNIQUE_NUMBER_RANGE)).thenReturn(UNIQUE_NUMBERS);
        Mockito.when(base62Encoder.encode(UNIQUE_NUMBERS)).thenReturn(HASHES);
        hashGenerator.generateBatch();
        Mockito.verify(hashRepository).save(HASHES);
    }
}
