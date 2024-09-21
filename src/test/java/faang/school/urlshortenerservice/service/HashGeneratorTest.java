package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {
    @Mock
    private HashRepository hashRepository;
    private int batchSize;
    private HashGenerator hashGenerator;
    private List<Long> numbers;
    private List<String> stringHashes;
    private List<Hash> hashes;

    @BeforeEach
    void setUp() {
        batchSize = 5;
        hashGenerator = new HashGenerator(hashRepository, batchSize);

        numbers = List.of(1L, 2L, 3L, 4L, 5L);
        stringHashes = List.of("T", "I", "W", "a", "X");
        hashes = new ArrayList<>();
        stringHashes.forEach(hash -> hashes.add(new Hash(hash)));
    }

    @Test
    void testGenerateBatch() {
        when(hashRepository.getUniqueNumbers(batchSize)).thenReturn(numbers);

        hashGenerator.generateBatch();
        verify(hashRepository, times(1)).getUniqueNumbers(batchSize);
        verify(hashRepository, times(1)).saveAll(hashes);
    }

    @Test
    void testGetHashesWhenNotEnoughInDB() {
        int amount = 6;
        List<Hash> hashesFromDB = new ArrayList<>(Arrays.asList(new Hash(), new Hash(), new Hash(), new Hash()));
        int initialSize = hashesFromDB.size();
        List<Hash> extraHashes = new ArrayList<>(Arrays.asList(new Hash(), new Hash()));
        List<Hash> expected = new ArrayList<>(List.copyOf(hashesFromDB));
        expected.addAll(extraHashes);

        when(hashRepository.getHashBatch(amount)).thenReturn(hashesFromDB);
        when(hashRepository.getUniqueNumbers(batchSize)).thenReturn(numbers);
        when(hashRepository.getHashBatch(amount - initialSize)).thenReturn(extraHashes);

        List<Hash> actual = hashGenerator.getHashes(amount);
        verify(hashRepository, times(1)).getHashBatch(amount);
        verify(hashRepository, times(1)).getUniqueNumbers(batchSize);
        verify(hashRepository, times(1)).getHashBatch(amount - initialSize);
        assertEquals(expected, actual);
    }

    @Test
    void testGetHashesWhenEnoughInDB() {
        int amount = 6;
        List<Hash> hashesFromDB = new ArrayList<>(
                Arrays.asList(new Hash(), new Hash(), new Hash(), new Hash(), new Hash(), new Hash()));

        when(hashRepository.getHashBatch(amount)).thenReturn(hashesFromDB);

        List<Hash> actual = hashGenerator.getHashes(amount);
        verify(hashRepository, times(1)).getHashBatch(amount);
        verify(hashRepository, times(0)).getUniqueNumbers(anyInt());
        verify(hashRepository, times(1)).getHashBatch(anyInt());
        assertEquals(hashesFromDB, actual);
    }

    @Test
    void getHashesAsync() {
        int amount = 6;
        List<Hash> hashesFromDB = new ArrayList<>(
                Arrays.asList(new Hash(), new Hash(), new Hash(), new Hash(), new Hash(), new Hash()));

        when(hashRepository.getHashBatch(amount)).thenReturn(hashesFromDB);

        CompletableFuture<List<Hash>> actual = hashGenerator.getHashesAsync(amount);
        verify(hashRepository, times(1)).getHashBatch(amount);
        verify(hashRepository, times(0)).getUniqueNumbers(anyInt());
        verify(hashRepository, times(1)).getHashBatch(anyInt());
        assertEquals(hashesFromDB, actual.join());
    }
}