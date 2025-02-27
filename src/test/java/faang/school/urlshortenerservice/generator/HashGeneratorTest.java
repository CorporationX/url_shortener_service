package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    @Test
    public void testGenerateHash() {
        // Arrange
        List<Long> range = List.of(1L, 2L, 3L);
        List<String> encodedHashes = List.of("a", "b", "c");
        when(hashRepository.getUniqueNumbers(anyLong())).thenReturn(range);
        when(base62Encoder.encode(range)).thenReturn(encodedHashes);

        // Act
        hashGenerator.generateHash();

        // Assert
        verify(hashRepository, times(1)).getUniqueNumbers(anyLong());
        verify(base62Encoder, times(1)).encode(range);
        verify(hashRepository, times(1)).saveAll(anyList());
    }

    @Test
    public void testGetHashes() {
        // Arrange
        List<Hash> hashes = List.of(new Hash("a"), new Hash("b"));
        when(hashRepository.getHashBatch(2)).thenReturn(hashes);

        // Act
        List<String> result = hashGenerator.getHashes(2);

        // Assert
        assertEquals(2, result.size());
        assertEquals(List.of("a", "b"), result);
        verify(hashRepository, times(1)).getHashBatch(2);
    }

}