package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @InjectMocks
    private HashGenerator hashGenerator;

    @Mock
    private HashRepository hashRepository;
    @Mock
    private Base62Encoder base62Encoder;

    @Test
    void testGenerateBatch() {
        int batch = 3;
        List<Long> numbers = List.of(1L, 2L, 3L);
        List<String> generatedHashes = List.of("1", "2", "3");
        List<Hash> hashes = List.of(new Hash("1"), new Hash("2"), new Hash("3"));

        when(hashRepository.getUniqueNumbers(batch)).thenReturn(numbers);
        when(base62Encoder.encode(numbers)).thenReturn(generatedHashes);
        when(hashRepository.saveAll(hashes)).thenReturn(hashes);

        hashGenerator.generateBatch(batch);

        verify(hashRepository, times(1)).getUniqueNumbers(batch);
        verify(base62Encoder, times(1)).encode(numbers);
        verify(hashRepository, times(1)).saveAll(hashes);
    }
}