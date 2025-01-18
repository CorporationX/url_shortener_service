package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.base62.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {
    @InjectMocks
    private HashGenerator hashGenerator;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @BeforeEach
    void setUp() throws Exception {
        Field uniqueNumberField = HashGenerator.class.getDeclaredField("uniqueNumber");
        uniqueNumberField.setAccessible(true);
        uniqueNumberField.set(hashGenerator, 1000L);
    }

    @Test
    void testGenerateBatch_Success() {
        List<Long> mockNumbers = List.of(1001L, 1002L, 1003L);
        List<Hash> mockHashes = List.of(
                new Hash("hash1"),
                new Hash("hash2"),
                new Hash("hash3")
        );

        when(hashRepository.getUniqueNumbers(1000L)).thenReturn(mockNumbers);
        when(base62Encoder.encode(mockNumbers)).thenReturn(mockHashes);

        hashGenerator.generateBatch();

        verify(hashRepository).getUniqueNumbers(1000L);
        verify(base62Encoder).encode(mockNumbers);
        verify(hashRepository).saveAll(mockHashes);
    }

    @Test
    void testGenerateBatch_Failure() {
        List<Long> mockNumbers = List.of(1001L, 1002L, 1003L);

        when(hashRepository.getUniqueNumbers(anyLong())).thenReturn(mockNumbers);
        when(base62Encoder.encode(mockNumbers)).thenThrow(new RuntimeException("Encoding failed"));

        Exception ex = assertThrows(RuntimeException.class, () -> hashGenerator.generateBatch());

        verify(hashRepository).getUniqueNumbers(anyLong());
        verify(base62Encoder).encode(mockNumbers);
        verify(hashRepository, never()).saveAll(anyList());

        assertEquals("Encoding failed", ex.getMessage());
    }
}
