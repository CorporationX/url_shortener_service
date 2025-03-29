package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.util.encoder.Base62Encoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorServiceTest {
    @Mock
    private HashRepository hashRepository;
    
    @Mock
    private Base62Encoder base62Encoder;
    
    @Mock
    private HashService hashService;
    
    @InjectMocks
    private HashGeneratorService hashGeneratorService;
    
    @Test
    void testGenerateBatch() {
        // Given
        int batchSize = 5;
        List<Long> uniqueNumbers = List.of(1L, 2L, 3L, 4L, 5L);
        List<String> encodedHashes = List.of("a", "b", "c", "d", "e");
        
        when(hashRepository.getUniqueNumbers(batchSize)).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(uniqueNumbers)).thenReturn(encodedHashes);
        
        // When
        hashGeneratorService.generateBatch(batchSize);
        
        // Then
        verify(hashRepository).getUniqueNumbers(batchSize);
        verify(base62Encoder).encode(uniqueNumbers);
        verify(hashService).saveHashes(encodedHashes);
    }
    
    @Test
    void testGenerateBatchWithZeroBatchSize() {
        // Given
        int batchSize = 0;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> hashGeneratorService.generateBatch(batchSize));
        verify(hashRepository, never()).getUniqueNumbers(batchSize);
    }

    @Test
    void testGenerateBatchWithNegativeBatchSize() {
        // Given
        int batchSize = -5;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> hashGeneratorService.generateBatch(batchSize));
        verify(hashRepository, never()).getUniqueNumbers(batchSize);
    }

}