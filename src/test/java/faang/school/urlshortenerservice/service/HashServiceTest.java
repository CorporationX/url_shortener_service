package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.hash.HashService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashServiceTest {

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private HashService hashService;

    @Test
    void testGenerateHashes() {
        int batchSize = 5;
        List<Long> uniqueNumbers = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        when(hashRepository.getUniqueNumbers(batchSize)).thenReturn(uniqueNumbers);

        hashService.generateHashes(batchSize);

        ArgumentCaptor<List<Hash>> hashListCaptor = ArgumentCaptor.forClass(List.class);
        verify(hashRepository).saveAll(hashListCaptor.capture());
        List<Hash> savedHashes = hashListCaptor.getValue();
        assertEquals(5, savedHashes.size());
    }

    @Test
    void testGetHashesWhenSizeIsGreaterThanCount() {
        int size = 3;
        when(hashRepository.count()).thenReturn(2L);
        when(hashRepository.findAndDeleteBySize(size)).thenReturn(List.of(new Hash("hash")));

        Mono<List<String>> result = hashService.getHashes(size);

        assertTrue(result.block().contains("hash"));
    }

    @Test
    void testGetHashesWhenSizeIsLessThanCount() {
        int size = 2;
        List<Hash> hashes = List.of(new Hash("hash1"), new Hash("hash2"));
        when(hashRepository.count()).thenReturn(3L);
        when(hashRepository.findAndDeleteBySize(size)).thenReturn(hashes);

        Mono<List<String>> result = hashService.getHashes(size);

        List<String> hashList = result.block();
        assertEquals(size, hashList.size());
        assertTrue(hashList.containsAll(List.of("hash1", "hash2")));
    }

    @Test
    void testGetHashCount() {
        long expectedCount = 15L;
        when(hashRepository.count()).thenReturn(expectedCount);

        Long actualCount = hashService.getHashCount();

        assertEquals(expectedCount, actualCount);
    }
}
