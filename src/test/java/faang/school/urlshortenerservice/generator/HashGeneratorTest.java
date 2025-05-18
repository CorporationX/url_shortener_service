package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {

    @Mock
    private HashJpaRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    @Test
    public void testGenerateBatch() throws Exception {
        List<Long> mockRange = Arrays.asList(1L, 2L, 3L);
        List<String> mockHashes = Arrays.asList("hash1", "hash2", "hash3");
        when(hashRepository.getNextRange(0)).thenReturn(mockRange);
        when(base62Encoder.applyBase62Encoding(mockRange)).thenReturn(mockHashes);
        hashGenerator.generateBatch();
        verify(hashRepository).getNextRange(0);
        verify(base62Encoder).applyBase62Encoding(mockRange);
        verify(hashRepository).saveAll(anyList());
    }
}