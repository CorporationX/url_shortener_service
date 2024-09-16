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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;
    @Mock
    private Base62Encoder base62Encoder;
    @InjectMocks
    private HashGenerator hashGenerator;
    List<Long> uniqueNumbers = List.of(1L, 2L);
    private final int batchSize = 2;
    private final long size = 10;


    @Test
    @DisplayName("generateAndSaveBatchGenerateBatchExceptionGetUniqueNumbers")
    void testGenerateBatchExceptionGetUniqueNumbers(){
        when(hashRepository.getUniqueNumbers(anyLong())).thenThrow(new RuntimeException("exception"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                hashGenerator.generateAndSaveBatch());

        assertEquals("exception", exception.getMessage());
    }

    @Test
    @DisplayName("generateAndSaveBatchGenerateBatchExceptionEncode")
    void testGenerateBatchExceptionEncode(){
        when(hashRepository.getUniqueNumbers(anyLong())).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(anyList())).thenThrow(new RuntimeException("exception"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                hashGenerator.generateAndSaveBatch());

        assertEquals("exception", exception.getMessage());
    }

    @Test
    @DisplayName("generateAndSaveBatchGenerateBatchException")
    void testGenerateBatchException(){
        when(hashGenerator.generateBatch(anyLong())).thenThrow(new RuntimeException("exception"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                hashGenerator.generateAndSaveBatch());

        assertEquals("exception", exception.getMessage());
    }

    @Test
    @DisplayName("generateAndSaveBatchSaveAllException")
    void testSaveAllException(){
        when(hashGenerator.generateBatch(anyLong())).thenReturn(List.of());
        when(hashRepository.saveAll(anyList())).thenThrow(new RuntimeException("exception"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                hashGenerator.generateAndSaveBatch());

        assertEquals("exception", exception.getMessage());
    }

    @Test
    @DisplayName("generateAndSaveBatchValid")
    void testGenerateAndSaveBatchValid(){
        when(hashGenerator.generateBatch(anyLong())).thenReturn(List.of());
        when(hashRepository.saveAll(anyList())).thenReturn(List.of());

        hashGenerator.generateAndSaveBatch();

        verify(hashRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("generateBatchHashRepositoryException")
    void testHashRepositoryException() {
        when(hashRepository.getUniqueNumbers(anyLong())).thenThrow(new RuntimeException("exception"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                hashGenerator.generateBatch(size));

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
                hashGenerator.generateBatch(size));

        assertEquals("exception", exception.getMessage());

        verify(hashRepository, times(1)).getUniqueNumbers(anyLong());
        verify(base62Encoder, times(1)).encode(anyList());
    }

    @Test
    @DisplayName("generateBatchValid")
    void testGenerateBatchValid() {
        List<Long> uniqueNumbers = List.of(1L, 2L);
        List<String> hashes = List.of("1L", "2L");

        when(hashRepository.getUniqueNumbers(anyLong())).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(anyList())).thenReturn(hashes);

        hashGenerator.generateBatch(size);

        verify(hashRepository, times(1)).getUniqueNumbers(anyLong());
        verify(base62Encoder, times(1)).encode(anyList());
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

        verify(hashRepository, times(1)).getHashBatch(anyInt());
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

    @Test
    @DisplayName("getHashBatchAsyncException")
    void testGetHashBatchAsyncException(){
        when(hashGenerator.getHashBatch(anyInt())).thenThrow(new RuntimeException("exception"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                hashGenerator.getHashBatchAsync(batchSize));

        assertEquals("exception", exception.getMessage());
    }

    @Test
    @DisplayName("getHashBatchAsync should execute asynchronously")
    void testGetHashBatchAsyncExecution() throws InterruptedException, ExecutionException {
        int batchSize = 5;
        List<String> expectedHashes = List.of("hash1", "hash2", "hash3", "hash4", "hash5");

        when(hashGenerator.getHashBatch(batchSize)).thenReturn(expectedHashes);

        long start = System.currentTimeMillis();

        CompletableFuture<List<String>> generalMethod = hashGenerator.getHashBatchAsync(batchSize);
        CompletableFuture<List<String>> time = CompletableFuture.supplyAsync(() ->
        {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return expectedHashes;
        });

        assertEquals(expectedHashes, generalMethod.join());

        long finish = System.currentTimeMillis() - start;
        assertTrue(finish < 500);
    }
}