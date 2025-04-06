package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.generator.Base62Encoder;
import faang.school.urlshortenerservice.service.generator.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    private final int TEST_MAX_RANGE = 100;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashGenerator, "maxRange", TEST_MAX_RANGE);
    }

    @Test
    void generateHashes_shouldSaveGeneratedHashes() {
        // Arrange
        List<Long> numbers = Arrays.asList(1L, 2L, 3L);
        List<Hash> hashes = Arrays.asList(new Hash("a"), new Hash("b"), new Hash("c"));

        when(hashRepository.getUniqueNumbers(TEST_MAX_RANGE)).thenReturn(numbers);
        when(base62Encoder.encode(numbers)).thenReturn(hashes);

        // Act
        hashGenerator.generateHashes();

        // Assert
        verify(hashRepository, times(1)).getUniqueNumbers(TEST_MAX_RANGE);
        verify(base62Encoder, times(1)).encode(numbers);
        verify(hashRepository, times(1)).saveAll(hashes);
    }

    @Test
    void getHashes_shouldThrowExceptionWhenCountIsZero() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> hashGenerator.getHashes(0)
        );

        assertEquals("Некорректное значение количества хэшей = 0", exception.getMessage());
    }

    @Test
    void getHashes_shouldReturnRequestedHashesWhenEnoughAvailable() {
        // Arrange
        int count = 3;
        List<Hash> availableHashes = Arrays.asList(new Hash("a"), new Hash("b"), new Hash("c"));

        when(hashRepository.findAndDelete(count)).thenReturn(availableHashes);

        // Act
        List<Hash> result = hashGenerator.getHashes(count);

        // Assert
        assertEquals(count, result.size());
        verify(hashRepository, times(1)).findAndDelete(count);
        verify(hashRepository, never()).saveAll(anyList());
    }

    @Test
    void getHashes_shouldGenerateNewHashesWhenNotEnoughAvailable() {
        int count = 6;

        when(hashRepository.findAndDelete(count))
                .thenReturn(new ArrayList<>(List.of(new Hash("a"), new Hash("b"))));

        when(hashRepository.findAndDelete(count - 2))
                .thenReturn(new ArrayList<>(List.of(new Hash("c"), new Hash("d"), new Hash("e"), new Hash("r"))));

        List<Hash> result = hashGenerator.getHashes(count);

        assertEquals(6, result.size());
    }

    @Test
    void getHashes_shouldNotRollbackOnIllegalArgumentException() {
        // Arrange
        when(hashRepository.findAndDelete(anyInt())).thenThrow(new IllegalArgumentException("DB error"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> hashGenerator.getHashes(5));

        // Проверяем, что транзакция не откатилась бы (в реальности нужно проверять через интеграционный тест)
        verify(hashRepository, times(1)).findAndDelete(anyInt());
    }

    @Test
    void getHashes_shouldReturnEmptyListWhenNoHashesAvailable() {
        // Arrange
        when(hashRepository.findAndDelete(anyInt())).thenReturn(Collections.emptyList());
        when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(Collections.emptyList());
        when(base62Encoder.encode(anyList())).thenReturn(Collections.emptyList());

        // Act
        List<Hash> result = hashGenerator.getHashes(3);

        // Assert
        assertTrue(result.isEmpty());
    }
}