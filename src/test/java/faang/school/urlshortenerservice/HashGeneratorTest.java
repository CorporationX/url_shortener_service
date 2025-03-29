package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.Base62Encoder;
import faang.school.urlshortenerservice.service.HashGenerator;
import faang.school.urlshortenerservice.service.HashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashService hashService;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    @BeforeEach
    void setUp() {
        hashGenerator = new HashGenerator(hashRepository, hashService, base62Encoder);
        lenient().when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(List.of(1L, 2L));
        lenient().when(base62Encoder.encode(anyList())).thenReturn(List.of("A", "B"));
    }

    @Test
    void testGenerateBatch_Success() {
        when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(List.of(1L, 2L));
        when(base62Encoder.encode(List.of(1L, 2L))).thenReturn(List.of("A", "B"));

        CompletableFuture<List<String>> future = hashGenerator.generateBatch();

        assertTrue(future.isDone());
        assertEquals(List.of("A", "B"), future.join());
        verify(hashService).saveHashes(List.of("A", "B"));
    }

    @Test
    void testGenerateBatch_EmptyUniqueNumbers() {
        when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(Collections.emptyList());
        when(base62Encoder.encode(Collections.emptyList())).thenReturn(Collections.emptyList());

        CompletableFuture<List<String>> future = hashGenerator.generateBatch();

        assertTrue(future.isDone());
        assertEquals(Collections.emptyList(), future.join());
        verify(hashService).saveHashes(Collections.emptyList());
    }

    @Test
    void testGenerateBatch_RepositoryThrowsException() {
        when(hashRepository.getUniqueNumbers(anyInt())).thenThrow(new RuntimeException("Database error"));

        CompletableFuture<List<String>> future = hashGenerator.generateBatch();

        assertTrue(future.isCompletedExceptionally());
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertEquals("Database error", exception.getCause().getMessage());
        verify(hashService, never()).saveHashes(anyList());
    }

    @Test
    void testGenerateBatch_EncoderReturnsDifferentSize() {
        when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(List.of(1L, 2L));
        when(base62Encoder.encode(List.of(1L, 2L))).thenReturn(List.of("A"));

        CompletableFuture<List<String>> future = hashGenerator.generateBatch();

        assertTrue(future.isDone());
        assertEquals(List.of("A"), future.join());
        verify(hashService).saveHashes(List.of("A"));
    }

    @Test
    void testInit_CallsGenerateBatch() {
        hashGenerator.init();

        verify(hashRepository).getUniqueNumbers(anyInt());
        verify(base62Encoder).encode(anyList());
        verify(hashService).saveHashes(anyList());
    }

    @Test
    void testGenerateBatch_LargeBatchSize() {
        List<Long> largeList = List.of(1L, 2L, 3L, 4L, 5L);
        when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(largeList);
        when(base62Encoder.encode(largeList)).thenReturn(List.of("A", "B", "C", "D", "E"));

        CompletableFuture<List<String>> future = hashGenerator.generateBatch();

        assertTrue(future.isDone());
        assertEquals(List.of("A", "B", "C", "D", "E"), future.join());
        verify(hashService).saveHashes(List.of("A", "B", "C", "D", "E"));
    }
}
