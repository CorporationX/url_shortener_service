package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.lenient;
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

    @BeforeEach
    void setUp() {
        lenient().when(base62Encoder.encode(anyLong())).thenAnswer(invocation ->
                "hash_" + invocation.getArgument(0));
    }

    @Test
    void testGenerateHashShouldCallSaveAllBatch() {
        int size = 5;
        List<String> generatedHashes = List.of("hash_1", "hash_2", "hash_3", "hash_4", "hash_5");

        when(hashRepository.getUniqueNumbers(size)).thenReturn(List.of(1L, 2L, 3L, 4L, 5L));

        hashGenerator.generateHash(size);

        verify(hashRepository, times(1)).saveAllBatch(eq(generatedHashes));
    }

    @Test
    void testGetHashesShouldReturnHashesFromRepository() {
        int batchSize = 3;
        List<String> storedHashes = new ArrayList<>(List.of("hash_AB", "hash_BC"));

        when(hashRepository.getHashAndDeleteFromDb(batchSize)).thenReturn(storedHashes);
        when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(List.of(1L));

        List<String> result = hashGenerator.getHashes(batchSize);

        assertEquals(batchSize, result.size());
        assertTrue(result.containsAll(storedHashes));
        assertTrue(result.contains("hash_1"));

         verify(hashRepository, times(1)).getHashAndDeleteFromDb(batchSize);
         verify(hashRepository, times(1)).getUniqueNumbers(1);
    }

    @Test
    void testGetHashesShouldGenerateHashesIfNoneInDb() {
        int batchSize = 3;

        when(hashRepository.getHashAndDeleteFromDb(batchSize)).thenReturn(new ArrayList<>());
        when(hashRepository.getUniqueNumbers(batchSize)).thenReturn(List.of(1L, 2L, 3L));

        List<String> result = hashGenerator.getHashes(batchSize);

        assertEquals(batchSize, result.size());
        assertTrue(result.containsAll(List.of("hash_1", "hash_2", "hash_3")));

        verify(hashRepository, times(1)).getHashAndDeleteFromDb(batchSize);
        verify(hashRepository, times(1)).getUniqueNumbers(batchSize);
    }

    @Test
    void testGenerateAndGetHashesShouldReturnEncodedNumbers() {
        int size = 3;
        List<Long> uniqueNumbers = List.of(100L, 200L, 300L);

        when(hashRepository.getUniqueNumbers(size)).thenReturn(uniqueNumbers);

        List<String> result = hashGenerator.getHashes(size);

        assertEquals(3, result.size());
        assertTrue(result.containsAll(List.of("hash_100", "hash_200", "hash_300")));

        verify(hashRepository, times(1)).getUniqueNumbers(size);
    }

    @Test
    void testGenerateAndGetHashesShouldThrowExceptionIfNoNumbers() {
        int size = 3;
        when(hashRepository.getUniqueNumbers(size)).thenReturn(new ArrayList<>());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            hashGenerator.getHashes(size);
        });

        assertEquals("uniqueNumbers is not read", exception.getMessage());
    }
}