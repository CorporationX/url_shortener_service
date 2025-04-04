package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.util.Base62Encoder;
import faang.school.urlshortenerservice.util.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashGenerator, "hashLength", 6);
    }

    @Test
    void generateBatch_shouldEncodeAndPadHashes() {
        // Arrange
        List<Long> numbers = List.of(1L, 2L, 1000L);
        List<String> encodedValues = List.of("1", "2", "g8");

        when(base62Encoder.encode(numbers)).thenReturn(encodedValues);

        // Act
        List<String> result = hashGenerator.generateBatch(numbers);

        // Assert
        assertEquals(3, result.size());
        assertEquals("000001", result.get(0));
        assertEquals("000002", result.get(1));
        assertEquals("0000g8", result.get(2));
    }

    @Test
    void generateBatch_shouldReturnProperLengthHashes() {
        // Arrange
        List<Long> numbers = List.of(123456789L);
        List<String> encodedValues = List.of("8m0Kx");

        when(base62Encoder.encode(numbers)).thenReturn(encodedValues);

        // Act
        List<String> result = hashGenerator.generateBatch(numbers);

        // Assert
        assertEquals(1, result.size());
        assertEquals("08m0Kx", result.get(0));
    }

    @Test
    void generateBatch_shouldNotPadWhenLengthExceedsRequired() {
        // Arrange
        List<Long> numbers = List.of(9999999999L);
        List<String> encodedValues = List.of("1EhF7HA");

        when(base62Encoder.encode(numbers)).thenReturn(encodedValues);

        // Act
        List<String> result = hashGenerator.generateBatch(numbers);

        // Assert
        assertEquals(1, result.size());
        assertEquals("1EhF7HA", result.get(0));
    }

    @Test
    void generateBatch_shouldHandleEmptyList() {
        // Arrange
        List<Long> numbers = List.of();
        List<String> encodedValues = List.of();

        when(base62Encoder.encode(numbers)).thenReturn(encodedValues);

        // Act
        List<String> result = hashGenerator.generateBatch(numbers);

        // Assert
        assertEquals(0, result.size());
    }
}