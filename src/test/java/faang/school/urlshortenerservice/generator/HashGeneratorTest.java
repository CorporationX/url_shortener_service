package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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

    private List<String> generatedHashes;
    private List<Hash> hashes;

    @BeforeEach
    void setUp() {
        generatedHashes = List.of("1", "2", "3");
        hashes = List.of(new Hash("1"), new Hash("2"), new Hash("3"));
    }

    @Test
    void testGenerateBatch() {
        List<Long> numbers = List.of(1L, 2L, 3L);

        when(hashRepository.getUniqueNumbers(any(Integer.class))).thenReturn(numbers);
        when(base62Encoder.encode(numbers)).thenReturn(generatedHashes);

        hashGenerator.generateBatch(any(Integer.class));

        verify(hashRepository, times(1)).getUniqueNumbers(any(Integer.class));
        verify(base62Encoder, times(1)).encode(numbers);
    }

    @Test
    void testGetHashBatchWithEnoughSize() {
        int amount = 3;
        when(hashRepository.getHashBatch(amount)).thenReturn(hashes);

        List<String> result = hashGenerator.getHashBatch(amount);

        verify(hashRepository, times(1)).getHashBatch(amount);
        assertEquals(generatedHashes, result);
    }

    @Test
    void testGetHashBatchWithoutEnoughSize() {
        int amount = 4;
        when(hashRepository.getHashBatch(amount)).thenReturn(new ArrayList<>(hashes));

        List<String> result = hashGenerator.getHashBatch(amount);

        verify(hashRepository, times(1)).getHashBatch(amount);

        assertEquals(generatedHashes, result);
    }
}