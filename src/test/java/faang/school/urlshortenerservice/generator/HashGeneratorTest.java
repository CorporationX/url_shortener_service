package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;
    @Mock
    private Base62Encoder base62Encoder;
    @InjectMocks
    private HashGenerator hashGenerator;
    private final int batchSize = 2;

    @BeforeEach
    void init() {

    }

    @Test
    @DisplayName("generateBatchHashRepositoryException")
    void testHashRepositoryException() {
        when(hashRepository.getUniqueNumbers(anyLong())).thenThrow(new RuntimeException("exception"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                hashGenerator.generateBatch());

        assertEquals("exception", exception.getMessage());

        verify(hashRepository, times(1)).getUniqueNumbers(anyLong());
    }

    @Test
    @DisplayName("generateBatchBase62EncoderException")
    void testBase62EncoderException() {
        List<Long> uniqueNumbers = List.of(1L, 2L);

        when(hashRepository.getUniqueNumbers(anyLong())).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(anyList())).thenThrow(new RuntimeException("exception"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                hashGenerator.generateBatch());

        assertEquals("exception", exception.getMessage());

        verify(hashRepository, times(1)).getUniqueNumbers(anyLong());
        verify(base62Encoder, times(1)).encode(anyList());
    }

    @Test
    @DisplayName("generateBatchHashRepositorySaveAllException")
    void testHashRepositorySaveAllException() {
        List<Long> uniqueNumbers = List.of(1L, 2L);
        List<String> hashes = List.of("1L", "2L");

        when(hashRepository.getUniqueNumbers(anyLong())).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(anyList())).thenReturn(hashes);
        when(hashRepository.saveAll(anyList())).thenThrow(new RuntimeException("exception"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                hashGenerator.generateBatch());

        assertEquals("exception", exception.getMessage());

        verify(hashRepository, times(1)).getUniqueNumbers(anyLong());
        verify(base62Encoder, times(1)).encode(anyList());
        verify(hashRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("generateBatchValid")
    void testGenerateBatchValid() {
        List<Long> uniqueNumbers = List.of(1L, 2L);
        List<String> hashes = List.of("1L", "2L");

        when(hashRepository.getUniqueNumbers(anyLong())).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(anyList())).thenReturn(hashes);
        when(hashRepository.saveAll(anyList())).thenReturn(List.of(new Hash()));

        hashGenerator.generateBatch();

        verify(hashRepository, times(1)).getUniqueNumbers(anyLong());
        verify(base62Encoder, times(1)).encode(anyList());
        verify(hashRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("getHashBatchHashRepositoryException")
    void testGetHashBatchHashRepositoryException() {
        when(hashRepository.getHashBatch(anyInt())).thenThrow(new RuntimeException("exception"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                hashGenerator.getHashBatch(batchSize));

        assertEquals("exception", exception.getMessage());

        verify(hashRepository, times(1)).getHashBatch(anyInt());
    }

    @Test
    @DisplayName("getHashBatchHashRepositoryOne")
    void testGetHashBatchHashRepositoryOne() {
        List<Hash> hashes = List.of(new Hash(), new Hash());

        when(hashRepository.getHashBatch(anyInt())).thenReturn(hashes);

        hashGenerator.getHashBatch(batchSize);

        verify(hashRepository, times(1)).getHashBatch(anyInt());
    }

    @Test
    @DisplayName("getHashBatchHashRepositoryTwo")
    void testGetHashBatchHashRepositoryTwo() {
        List<Hash> hashes = new ArrayList<>(List.of(new Hash()));
        List<Hash> newHashes = List.of(new Hash());

        when(hashRepository.getHashBatch(anyInt()))
                .thenReturn(hashes)
                .thenReturn(newHashes);

        hashGenerator.getHashBatch(batchSize);

        verify(hashRepository, times(2)).getHashBatch(anyInt());
    }

    @Test
    @DisplayName("getHashBatchAsync")
    void testGetHashBatchAsync() throws ExecutionException, InterruptedException {
        List<String> hashes = List.of("hash1", "hash2");
        HashGenerator hashGenerator = mock(HashGenerator.class);

        CompletableFuture<List<String>> futureHashes = CompletableFuture.completedFuture(hashes);
        doReturn(futureHashes).when(hashGenerator).getHashBatchAsync(anyInt());

        CompletableFuture<List<String>> future = hashGenerator.getHashBatchAsync(batchSize);

        assertEquals(hashes.size(), future.get().size());

        verify(hashGenerator, times(1)).getHashBatchAsync(anyInt());
    }
}