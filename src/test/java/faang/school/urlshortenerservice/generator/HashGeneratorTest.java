package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {
    @Mock
    private HashRepository hashRepository;
    @Mock
    private Base62Encoder base62Encoder;
    @InjectMocks
    private HashGenerator hashGenerator;

    @Test
    void testGenerateBatch() {
        int numberOfValue = 10;
        List<Long> uniqueNumbers = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
        List<String> hashes = List.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j");
        when(hashRepository.getUniqueValue(numberOfValue)).thenReturn(uniqueNumbers);
        when(base62Encoder.encodeListOfNumbers(uniqueNumbers)).thenReturn(hashes);

        hashGenerator.generateBatch(numberOfValue);

        verify(hashRepository, times(1)).save(hashes);
        verify(base62Encoder,times(1)).encodeListOfNumbers(uniqueNumbers);
        verify(hashRepository, times(1)).getUniqueValue(numberOfValue);
    }
}