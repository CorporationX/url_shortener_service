package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class HashServiceTest {


    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private HashService hashService;

    @BeforeEach
    public void setUp() {
        hashService = new HashService(hashRepository);
    }

    @Test
    public void testGetUniqueNumbers() {
        List<Long> uniqueNumbers = List.of(1L, 2L, 3L);
        when(hashRepository.getUniqueNumbers(1000)).thenReturn(uniqueNumbers);

        List<Long> result = hashService.getUniqueNumbers(1000);

        assertEquals(uniqueNumbers, result);
        verify(hashRepository, times(1)).getUniqueNumbers(1000);
    }

    @Test
    public void testSaveHashes() {
        List<Hash> hashes = List.of(new Hash("hash1"), new Hash("hash2"));
        when(hashRepository.saveAll(hashes)).thenReturn(hashes);

        List<Hash> result = hashService.saveHashes(hashes);

        assertEquals(hashes, result);
        verify(hashRepository, times(1)).saveAll(hashes);
    }

    @Test
    public void testGetHashBatch() {
        List<String> hashBatch = List.of("hash1", "hash2");
        when(hashRepository.getHashBatch(2)).thenReturn(hashBatch);

        List<String> result = hashService.getHashBatch(2);

        assertEquals(hashBatch, result);
        verify(hashRepository, times(1)).getHashBatch(2);
    }

    @Test
    public void testGetHashCount() {
        long count = 10L;
        when(hashRepository.count()).thenReturn(count);

        long result = hashService.getHashCount();

        assertEquals(count, result);
        verify(hashRepository, times(1)).count();
    }
}
