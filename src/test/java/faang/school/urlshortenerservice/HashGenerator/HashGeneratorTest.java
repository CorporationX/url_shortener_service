package faang.school.urlshortenerservice.HashGenerator;

import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @Mock
    private Base62Encoder base62Encoder;
    @Mock
    private HashRepository repository;

    private int hashCount = 3;

    private HashGenerator hashGenerator;

    @BeforeEach
    public void setUp() {
        hashGenerator = new HashGenerator(
                //hashCount,
                base62Encoder,
                repository);
    }

    @Test
    void positiveGenerateBatch() {
        List<Long> numbers = List.of(1L, 2L, 3L);
        List<String> hashes = List.of("a", "b", "c");

        when(repository.getUniqueNumbers(hashCount)).thenReturn(numbers);
        when(base62Encoder.encode(numbers)).thenReturn(hashes);
        doNothing().when(repository).saveHashes(hashes);

        CompletableFuture<Void> future = hashGenerator.generateBatch();

        assertDoesNotThrow(() -> future.get());
        verify(repository).getUniqueNumbers(hashCount);
        verify(base62Encoder).encode(numbers);
        verify(repository).saveHashes(hashes);
    }

    @Test
    void negativeGenerateBatchHashesNull() {
        List<String> emptyHashes = List.of();


        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> hashGenerator.saveHashes(emptyHashes)
        );

        assertEquals("В списке нет хешей", exception.getMessage());
        verify(repository, never()).saveHashes(any());
    }

    @Test
    void negativeGenerateBatchNumZero() {
        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> hashGenerator.fetchUniqueNumbers(0)
        );

        assertEquals("Количество хешей должно быть положительным", exception.getMessage());
        verify(repository, never()).getUniqueNumbers(anyInt());
    }
}