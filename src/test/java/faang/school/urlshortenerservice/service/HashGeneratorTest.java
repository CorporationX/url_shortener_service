package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashGenerator, "batch", 10);
    }

    @Test
    void givenValidBatchSize_whenGenerateBatch_thenGeneratesAndSavesHashes() {
        List<Long> numbers = List.of(1L, 2L, 3L);
        List<String> hashes = List.of("hash1", "hash2", "hash3");
        when(hashRepository.getUniqueNumbers(10)).thenReturn(numbers);
        when(base62Encoder.encodeBatch(numbers)).thenReturn(hashes);
        doNothing().when(hashRepository).save(hashes);

        hashGenerator.generateBatch();

        verify(hashRepository, times(1)).getUniqueNumbers(10);
        verify(base62Encoder, times(1)).encodeBatch(numbers);
        verify(hashRepository, times(1)).save(hashes);
    }

    @Test
    void givenEmptyNumbersList_whenGenerateBatch_thenSavesEmptyList() {
        when(hashRepository.getUniqueNumbers(10)).thenReturn(Collections.emptyList());
        when(base62Encoder.encodeBatch(Collections.emptyList())).thenReturn(Collections.emptyList());
        doNothing().when(hashRepository).save(Collections.emptyList());

        hashGenerator.generateBatch();

        verify(hashRepository, times(1)).getUniqueNumbers(10);
        verify(base62Encoder, times(1)).encodeBatch(Collections.emptyList());
        verify(hashRepository, times(1)).save(Collections.emptyList());
    }

    @Test
    void givenRepositoryThrowsException_whenGenerateBatch_thenPropagatesException() {
        RuntimeException exception = new RuntimeException("Database error");
        when(hashRepository.getUniqueNumbers(10)).thenThrow(exception);

        assertThrows(RuntimeException.class, () -> hashGenerator.generateBatch());
        verify(hashRepository, times(1)).getUniqueNumbers(10);
        verifyNoInteractions(base62Encoder);
        verifyNoMoreInteractions(hashRepository);
    }

    @Test
    void givenEncoderThrowsException_whenGenerateBatch_thenPropagatesException() {
        List<Long> numbers = List.of(1L, 2L);
        RuntimeException exception = new RuntimeException("Encoding error");
        when(hashRepository.getUniqueNumbers(10)).thenReturn(numbers);
        when(base62Encoder.encodeBatch(numbers)).thenThrow(exception);

        assertThrows(RuntimeException.class, () -> hashGenerator.generateBatch());
        verify(hashRepository, times(1)).getUniqueNumbers(10);
        verify(base62Encoder, times(1)).encodeBatch(numbers);
        verifyNoMoreInteractions(hashRepository);
    }

    @Test
    void givenZeroBatchSize_whenGenerateBatch_thenProcessesEmptyBatch() {
        ReflectionTestUtils.setField(hashGenerator, "batch", 0);
        when(hashRepository.getUniqueNumbers(0)).thenReturn(Collections.emptyList());
        when(base62Encoder.encodeBatch(Collections.emptyList())).thenReturn(Collections.emptyList());
        doNothing().when(hashRepository).save(Collections.emptyList());

        hashGenerator.generateBatch();

        verify(hashRepository, times(1)).getUniqueNumbers(0);
        verify(base62Encoder, times(1)).encodeBatch(Collections.emptyList());
        verify(hashRepository, times(1)).save(Collections.emptyList());
    }
}
