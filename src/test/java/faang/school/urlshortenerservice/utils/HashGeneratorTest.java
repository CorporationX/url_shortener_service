package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    private static final int TEST_BATCH_SIZE = 1000;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    private HashGenerator hashGenerator;

    @BeforeEach
    void setUp() {
        hashGenerator = new HashGenerator(TEST_BATCH_SIZE, hashRepository, base62Encoder);
    }

    @Test
    void testGenerateHashesShouldProcessBatchSuccessfully() {

        List<Long> testNumbers = List.of(1L, 2L, 3L);
        List<String> testHashes = List.of("a", "b", "c");

        when(hashRepository.getUniqueNumbers(TEST_BATCH_SIZE)).thenReturn(testNumbers);
        when(base62Encoder.encode(testNumbers)).thenReturn(testHashes);

        hashGenerator.generateHashes();

        verify(hashRepository).getUniqueNumbers(TEST_BATCH_SIZE);
        verify(base62Encoder).encode(testNumbers);
        verify(hashRepository).saveAllBatch(testHashes);
    }

    @Test
    void testGenerateHashesWhenEmptyNumbersShouldNotCallEncoderOrSave() {

        when(hashRepository.getUniqueNumbers(TEST_BATCH_SIZE)).thenReturn(List.of());

        hashGenerator.generateHashes();

        verify(hashRepository).getUniqueNumbers(TEST_BATCH_SIZE);
        verifyNoInteractions(base62Encoder);
        verify(hashRepository, never()).saveAllBatch(any());
    }

    @Test
    void testGenerateHashesWhenEncoderThrowsExceptionShouldNotSave() {

        List<Long> testNumbers = List.of(1L, 2L, 3L);

        when(hashRepository.getUniqueNumbers(TEST_BATCH_SIZE)).thenReturn(testNumbers);
        when(base62Encoder.encode(testNumbers)).thenThrow(new RuntimeException("Encoding failed"));

        hashGenerator.generateHashes();

        verify(hashRepository).getUniqueNumbers(TEST_BATCH_SIZE);
        verify(base62Encoder).encode(testNumbers);
        verify(hashRepository, never()).saveAllBatch(any());
    }
}