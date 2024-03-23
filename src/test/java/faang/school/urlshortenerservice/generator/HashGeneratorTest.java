package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.Base62Encoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @InjectMocks
    private HashGenerator hashGenerator;
    @Mock
    private HashRepository hashRepository;
    @Mock
    private Base62Encoder base62Encoder;

    private final List<Long> UNIQUE_NUMBERS = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
    private final List<String> HASHES = List.of("qw", "de", "rt", "yt", "kg", "gk", "iy", "lk", "oi", "bn");
    private final int UNIQUE_NUMBER_RANGE = 10;

    @Test
    void testGenerateHash() {
        ReflectionTestUtils.setField(hashGenerator, "uniqueNumberRange", UNIQUE_NUMBER_RANGE);
        when(hashRepository.getUniqueNumbers(UNIQUE_NUMBER_RANGE)).thenReturn(UNIQUE_NUMBERS);
        when(base62Encoder.encode(UNIQUE_NUMBERS)).thenReturn(HASHES);
        hashGenerator.generateHash();
        verify(hashRepository, times(1)).save(HASHES);
    }

}
