package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import faang.school.urlshortenerservice.util.HashGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    @Captor
    private ArgumentCaptor<List<Hash>> hashesCaptor;

    @Test
    void generateBatch_shouldCreateAndSaveHashes() {
        int batchSize = 3;
        ReflectionTestUtils.setField(hashGenerator, "batchSize", batchSize);

        List<Long> numbers = List.of(1L, 2L, 3L);
        List<String> hashes = List.of("a", "b", "c");

        when(hashRepository.findTopN(batchSize)).thenReturn(numbers);
        when(base62Encoder.encode(numbers)).thenReturn(hashes);

        hashGenerator.generateBatch();
        verify(hashRepository).findTopN(batchSize);
        verify(base62Encoder).encode(numbers);
        verify(hashRepository).saveAll(hashesCaptor.capture());

        List<Hash> savedHashes = hashesCaptor.getValue();
        assertEquals(3, savedHashes.size());

        for (int i = 0; i < savedHashes.size(); i++) {
            Hash hash = savedHashes.get(i);
            assertEquals(numbers.get(i), hash.getNumber());
            assertEquals(hashes.get(i), hash.getHash());
            assertTrue(hash.isLock());
        }
    }

    @Test
    void generateBatch_whenNoNumbersFound_shouldNotSaveHashes() {
        int batchSize = 5;
        ReflectionTestUtils.setField(hashGenerator, "batchSize", batchSize);

        when(hashRepository.findTopN(batchSize)).thenReturn(List.of());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            hashGenerator.generateBatch();
        });

        assertEquals("There are no free Numbers for generating new hashes!", exception.getMessage());
        verify(hashRepository).findTopN(batchSize);
    }

    @Test
    void generateBatch_shouldUseCorrectBatchSize() {
        int batchSize = 10;
        ReflectionTestUtils.setField(hashGenerator, "batchSize", batchSize);

        List<Long> numbers = List.of(1L);
        List<String> hashes = List.of("a");

        when(hashRepository.findTopN(batchSize)).thenReturn(numbers);
        when(base62Encoder.encode(numbers)).thenReturn(hashes);

        hashGenerator.generateBatch();

        verify(hashRepository).findTopN(batchSize);
    }
}
