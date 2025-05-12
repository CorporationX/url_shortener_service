package faang.school.urlshortenerservice.component;

import faang.school.urlshortenerservice.config.app.HashGeneratorConfig;
import faang.school.urlshortenerservice.repository.interfaces.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HashGeneratorTest {
    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @Mock
    private HashGeneratorConfig hashGeneratorConfig;

    @InjectMocks
    private HashGenerator hashGenerator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(hashGeneratorConfig.getBatchSize()).thenReturn(3);
    }

    @Test
    public void testGenerateBatch() {
        List<Long> mockNumbers = Arrays.asList(1L, 2L, 3L);
        List<String> mockHashes = Arrays.asList("000001", "000002", "000003");

        when(hashRepository.getUniqueNumbers(3)).thenReturn(mockNumbers);
        when(base62Encoder.encode(mockNumbers)).thenReturn(mockHashes);

        hashGenerator.generateBatch();

        verify(hashRepository).getUniqueNumbers(3);
        verify(base62Encoder).encode(mockNumbers);
        verify(hashRepository).save(mockHashes);
    }
}