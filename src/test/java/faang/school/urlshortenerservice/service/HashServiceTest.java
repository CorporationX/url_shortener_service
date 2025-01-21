package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.jpa.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashServiceTest {
    @InjectMocks
    HashService hashService;
    @Mock
    HashRepository hashRepository;

    @Test
    void testGetUniqueNumbers_Success() {
        int count = 10;
        List<Long> uniqueNumbers = List.of(1L, 2L, 3L);

        when(hashRepository.getUniqueNumbers(count)).thenReturn(uniqueNumbers);

        List<Long> result = hashService.getUniqueNumbers(count);

        assertNotNull(result);
        assertEquals(uniqueNumbers, result);
        verify(hashRepository, times(1)).getUniqueNumbers(count);
    }

    @Test
    void testSaveAll_Success() {
        List<Hash> hashes = List.of(new Hash("hash1"), new Hash("hash2"));
        List<Hash> savedHashes = List.of(new Hash("hash3"), new Hash("hash4"));

        when(hashRepository.saveAll(hashes)).thenReturn(savedHashes);

        List<Hash> result = hashService.saveAll(hashes);

        assertNotNull(result);
        assertEquals(savedHashes, result);
        verify(hashRepository, times(1)).saveAll(hashes);
    }

    @Test
    void testGetHashBatch_Success() {
        int count = 5;
        List<String> hashBatch = List.of("hash1", "hash2", "hash3");

        when(hashRepository.getHashBatch(count)).thenReturn(hashBatch);

        CompletableFuture<List<String>> resultFuture = hashService.getHashBatch(count);
        List<String> result = resultFuture.join();

        assertNotNull(result);
        assertEquals(hashBatch, result);
        verify(hashRepository, times(1)).getHashBatch(count);
    }

    @Test
    void testCount_Success() {
        Long expectedCount = 100L;

        when(hashRepository.count()).thenReturn(expectedCount);

        Long result = hashService.count();

        assertNotNull(result);
        assertEquals(expectedCount, result);
        verify(hashRepository, times(1)).count();
    }
}