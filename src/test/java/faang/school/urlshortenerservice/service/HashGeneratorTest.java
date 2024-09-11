package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
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

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashGenerator, "maxRange", 5L);
    }

    @Test
    void testGenerateBatch() {
        List<Long> mockNumbers = List.of(1L, 2L, 3L, 4L, 5L);
        when(hashRepository.getUniqueNumbers(5L)).thenReturn(mockNumbers);

        List<String> mockHashes = List.of("abc", "def", "ghi", "jkl", "mno");
        when(base62Encoder.encoder(mockNumbers)).thenReturn(mockHashes);

        hashGenerator.generateBatch();

        verify(hashRepository, times(1)).getUniqueNumbers(5L);
        verify(base62Encoder, times(1)).encoder(mockNumbers);

        verify(hashRepository, times(1)).saveAll(anyList());
    }
}
