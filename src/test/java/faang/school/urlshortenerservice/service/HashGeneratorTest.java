package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.hashGenerator.HashGeneratorConfig;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {

    @InjectMocks
    private HashGenerator hashGenerator;

    @Mock
    private Base62Encoder base62Encoder;
    @Mock
    private HashRepository hashRepository;
    @Mock
    private HashGeneratorConfig hashGeneratorConfig;

    @BeforeEach
    void setUp() {
        when(hashGeneratorConfig.getBatchSize()).thenReturn(5);
    }

    @Test
    public void testGenerateBatch() {
        List<Long> mockNumbers = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(mockNumbers);
        when(base62Encoder.encode(mockNumbers)).thenReturn(Arrays.asList("a", "b", "c", "d", "e"));

        hashGenerator.generateBatch();

        verify(hashRepository).getUniqueNumbers(5);
        verify(base62Encoder).encode(mockNumbers);
        verify(hashRepository).saveAll(anyList());
    }
}
