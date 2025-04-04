package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private HashGenerator hashGenerator;

    private final long CAPACITY = 10000L;

    @BeforeEach
    void setUp() {
        lenient().when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(List.of(1L, 2L, 3L));
        lenient().when(hashRepository.getHashBatch(anyLong()))
                .thenReturn(List.of(new Hash(1L,"abc"), new Hash(2L,"def")));
    }

    @Test
    void testGenerateHash_ShouldGenerateAndSaveHashes() {
        hashGenerator.generateHash();
        verify(hashRepository, times(1)).getUniqueNumbers(anyInt());
        verify(hashRepository, times(1)).save(anyList());
    }

    @Test
    void testGetHashes_ShouldReturnHashes() {
        List<String> hashes = hashGenerator.getHashes(2);
        assertEquals(2, hashes.size());
        assertTrue(hashes.contains("abc"));
        assertTrue(hashes.contains("def"));
    }

    @Test
    void testGetHashes_ShouldGenerateHashesIfNeeded() {
        when(hashRepository.getHashBatch(anyLong())).thenReturn(new ArrayList<>());
        hashGenerator.getHashes(CAPACITY);
        verify(hashRepository, times(2)).getHashBatch(anyLong());
        verify(hashRepository, times(1)).getUniqueNumbers(anyInt());
    }

    @Test
    void testGetHashesAsync_ShouldReturnHashesAsynchronously() {
        CompletableFuture<List<String>> futureHashes = hashGenerator.getHashesAsync(2);
        assertNotNull(futureHashes);
        List<String> hashes = futureHashes.join();
        assertEquals(2, hashes.size());
        assertTrue(hashes.contains("abc"));
        assertTrue(hashes.contains("def"));
    }
}
