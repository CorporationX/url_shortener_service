package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HashGeneratorTest {

    private HashGenerator hashGenerator;
    private HashRepository hashRepository;
    private Base62Encoder base62Encoder;

    @BeforeEach
    void setUp() {
        hashRepository = mock(HashRepository.class);
        base62Encoder = mock(Base62Encoder.class);
        hashGenerator = new HashGenerator(hashRepository, base62Encoder);

        ReflectionTestUtils.setField(hashGenerator, "batchSize", 5);
    }

    @Test
    void testGenerateBatch() {
        List<Long> uniqueNumbers = List.of(1L, 2L, 3L, 4L, 5L);
        List<String> encodedHashes = List.of("1", "2", "3", "4", "5");

        when(hashRepository.getUniqueNumbers(5)).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(uniqueNumbers)).thenReturn(encodedHashes);

        hashGenerator.generateBatch();
        verify(hashRepository, times(1)).getUniqueNumbers(5);
        verify(base62Encoder, times(1)).encode(uniqueNumbers);

        ArgumentCaptor<List<Hash>> captor = ArgumentCaptor.forClass(List.class);
        verify(hashRepository, times(1)).saveAll(captor.capture());

        List<Hash> savedHashes = captor.getValue();
        assertEquals(5, savedHashes.size(), "Expected 5 hashes to be saved");
        assertEquals("1", savedHashes.get(0).getHash(), "Expected first hash to be '1'");
    }

    @Test
    void testGenerateBatchLogs() {
        List<Long> uniqueNumbers = List.of(10L, 20L);
        List<String> encodedHashes = List.of("A", "B");

        when(hashRepository.getUniqueNumbers(5)).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(uniqueNumbers)).thenReturn(encodedHashes);

        hashGenerator.generateBatch();
        verify(hashRepository, times(1)).getUniqueNumbers(5);
        verify(base62Encoder, times(1)).encode(uniqueNumbers);
    }
}
