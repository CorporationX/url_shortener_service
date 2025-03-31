package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashGenerator;
import faang.school.urlshortenerservice.util.Base62Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @Mock
    private Base62Encoder encoder;

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private HashGenerator hashGenerator;

    private int minHashAmount = 10;
    private int hashLimit = 100;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashGenerator, "minHashAmountInDatabase", minHashAmount);
        ReflectionTestUtils.setField(hashGenerator, "hashLimit", hashLimit);
    }

    @Test
    public void testGenerateBatches_SkipWhenEnoughHashes() {
        when(hashRepository.count()).thenReturn(15L);

        hashGenerator.generateBatches();

        verify(hashRepository, never()).getUniqueNumbers(anyInt());
        verify(encoder, never()).generateHashes(any());
        verify(hashRepository, never()).saveHashes(any());
    }

    @Test
    public void testGenerateBatches_GenerateAndSaveHashes() {
        when(hashRepository.count()).thenReturn(5L);
        List<Long> uniqueNumbers = Arrays.asList(1L, 2L, 3L);
        when(hashRepository.getUniqueNumbers(hashLimit)).thenReturn(uniqueNumbers);
        String[] generatedHashes = new String[]{"hash1", "hash2", "hash3"};
        when(encoder.generateHashes(uniqueNumbers)).thenReturn(generatedHashes);

        hashGenerator.generateBatches();

        verify(hashRepository, times(1)).getUniqueNumbers(hashLimit);
        verify(encoder, times(1)).generateHashes(uniqueNumbers);
        verify(hashRepository, times(1)).saveHashes(generatedHashes);
    }
}
