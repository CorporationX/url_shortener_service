package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @Mock
    HashService hashService;

    @Mock
    Base62Encoder base62Encoder;

    @InjectMocks
    HashGenerator hashGenerator;

    @Test
    void testGenerateBatch() {
        List<Long> numbers = List.of(1L, 2L, 3L);
        List<String> hashes = List.of("hash1", "hash2", "hash3");
        when(hashService.getUniqueNumbers()).thenReturn(numbers);
        when(base62Encoder.encodeList(numbers)).thenReturn(hashes);

        hashGenerator.generateBatch();

        verify(hashService).save(hashes);
    }
}