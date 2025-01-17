package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {
    @Mock
    private HashRepository hashRepository;
    @Mock
    private Base62Encoder base62Encoder;

    private long sequenceAmount = 5;

    @InjectMocks
    private HashGenerator hashGenerator;

    @Test
    public void testGenerateBatch_Success() {
        List<Long> sequences = List.of(1L, 2L, 3L, 4L, 5L);
        List<String> encodedHashes = List.of("a", "b", "c", "d", "e");

        when(hashRepository.getUniqueNumbers(anyLong())).thenReturn(sequences);
        when(base62Encoder.encode(sequences)).thenReturn(encodedHashes);

        hashGenerator.generateBatch();

        verify(hashRepository, times(1)).getUniqueNumbers(anyLong());
        verify(base62Encoder, times(1)).encode(sequences);
        verify(hashRepository, times(1)).save(encodedHashes);
    }

    @Test
    public void testGenerateBatch_Exception() {
        when(hashRepository.getUniqueNumbers(anyLong()))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            hashGenerator.generateBatch();
        });

        assertEquals("java.lang.RuntimeException: Database error", exception.getMessage());
        verify(hashRepository,times(1)).getUniqueNumbers(anyLong());
        verifyNoInteractions(base62Encoder);
        verifyNoMoreInteractions(hashRepository);
    }
}