package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.config.properties.HashProperties;
import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.exception.HashGenerationException;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder encoder;

    @Mock
    private HashProperties hashProperties;

    @InjectMocks
    private HashGenerator hashGenerator;

    private final List<Long> testNumbers = List.of(1L, 2L, 3L);
    private final List<String> testHashes = List.of("a", "b", "c");
    private final List<Hash> testHashEntities = List.of(
            Hash.builder().hash("a").build(),
            Hash.builder().hash("b").build(),
            Hash.builder().hash("c").build()
    );

    @Test
    void testGetHashesWhenEnoughInDatabase() {
        when(hashProperties.getBatchSize()).thenReturn(3);
        when(hashRepository.getHashBatch(3)).thenReturn(testHashEntities);

        List<String> result = hashGenerator.getHashes();

        assertEquals(testHashes, result);
        verify(hashRepository).getHashBatch(3);
        verifyNoMoreInteractions(hashRepository);
    }

    @Test
    void testGenerateBatchSuccessfully() {
        when(hashProperties.getMaxRange()).thenReturn(1000);
        when(hashRepository.getUniqueNumbers(1000)).thenReturn(testNumbers);
        when(encoder.encode(testNumbers)).thenReturn(testHashes);

        hashGenerator.generateBatch();

        verify(hashRepository).saveAll(testHashEntities);
    }

    @Test
    void testGenerateBatchWhenThrowException() {
        when(hashProperties.getMaxRange()).thenReturn(1000);
        when(hashRepository.getUniqueNumbers(1000)).thenThrow(new HashGenerationException("Test error"));

        assertThrows(HashGenerationException.class, () -> hashGenerator.generateBatch());
    }
}
