package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.config.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@EnableAsync
class HashGeneratorTest {
    @Mock
    private HashRepository hashRepository;
    @Mock
    private Base62Encoder base62Encoder;
    @InjectMocks
    private HashGenerator hashGenerator;

    @Test
    void positiveGenerateBatch() {
        List<Long> forHashGenerate = List.of(1L, 2L, 3L);
        List<Hash> hashes = List.of(new Hash("1"), new Hash("2"), new Hash("3"));

        when(hashRepository.getUniqueNumbers(anyLong())).thenReturn(forHashGenerate);
        when(base62Encoder.encode(forHashGenerate)).thenReturn(hashes);
        when(hashRepository.saveAll(hashes)).thenReturn(null);

        hashGenerator.generateBatch();

        verify(hashRepository, times(1)).getUniqueNumbers(anyLong());
        verify(base62Encoder, times(1)).encode(forHashGenerate);
        verify(hashRepository, times(1)).saveAll(hashes);
    }

    @Test
    void negativeGenerateBatch() {
        List<Long> forHashGenerate = List.of(1L, 2L, 3L);
        when(hashRepository.getUniqueNumbers(anyLong())).thenReturn(forHashGenerate);
        when(base62Encoder.encode(anyList())).thenThrow(new RuntimeException("Exception"));

        Exception ex = assertThrows(RuntimeException.class, () -> hashGenerator.generateBatch());

        verify(hashRepository, times(1)).getUniqueNumbers(anyLong());
        verify(hashRepository, never()).saveAll(anyList());
        assertEquals("Exception", ex.getMessage());
    }
}