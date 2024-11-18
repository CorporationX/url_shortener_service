package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {
    private HashGenerator hashGenerator;

    @Mock
    private HashRepository hashRepository;
    @Mock
    private Base62Encoder base62Encoder;

    @Captor
    private ArgumentCaptor<List<Hash>> hashCaptor;
    private final int batchSize = 100;
    private List<String> hashStrings;
    private List<Long> numbers;
    private List<Hash> expectedHashes;

    @BeforeEach
    public void setup() {
        hashGenerator = new HashGenerator(
            hashRepository,
            base62Encoder,
            batchSize
        );
        numbers = new ArrayList<>(
            Arrays.asList(
                1L, 2L, 3L, 4L, 5L
            )
        );
        hashStrings = new ArrayList<>(
            Arrays.asList(
                "1", "2", "3", "4", "5"
            )
        );
        expectedHashes = new ArrayList<>(
            Arrays.asList(
                createHash("1"),
                createHash("2"),
                createHash("3"),
                createHash("4"),
                createHash("5")
            )
        );
    }

    @Test
    public void testGenerateBatch() {
        // Arrange
        when(hashRepository.getUniqueNumbers(batchSize)).thenReturn(numbers);
        when(base62Encoder.encode(numbers)).thenReturn(hashStrings);

        // Act
        hashGenerator.generateBatch();
        verify(hashRepository, times(1)).saveAll(hashCaptor.capture());

        // Assert
        assertEquals(expectedHashes, hashCaptor.getValue());

    }

    private Hash createHash(String hash) {
        return Hash.builder().hash(hash).build();
    }
}
