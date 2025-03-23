package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.BaseEncoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private BaseEncoder baseEncoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    private List<Long> uniqueNumbers;

    @BeforeEach
    public void setUp() {
        uniqueNumbers = List.of(1L, 2L, 3L);
    }

    @Test
    void testGenerateHash_Success() {
        when(hashRepository.getUniqueNumbers(anyLong()))
                .thenReturn(uniqueNumbers);
        List<Hash> hashes = Arrays.asList(
                new Hash("hash1"),
                new Hash("hash2"),
                new Hash("hash3"));
        when(baseEncoder.encode(anyList())).thenReturn(hashes);

        hashGenerator.generateHash();

        verify(hashRepository).saveAll(hashes);
    }

    @Test
    void testGenerateHash_NoUniqueNumbers() {
        when(hashRepository.getUniqueNumbers(anyLong()))
                .thenReturn(Collections.emptyList());

        assertThrows(EntityNotFoundException.class,
                () -> hashGenerator.generateHash());
    }

    @Test
    void testGetHashes_EnoughHashes() {
        List<String> hashes = List.of("hash1", "hash2");
        when(hashRepository.getAndDeleteHashBatch(anyLong()))
                .thenReturn(hashes);

        List<String> result = hashGenerator.getHashes();

        assertEquals(hashes, result);
    }

    @Test
    void testGetHashesAsync() throws Exception {
        List<String> hashes = List.of("hash1", "hash2");
        when(hashRepository.getAndDeleteHashBatch(anyLong()))
                .thenReturn(hashes);

        CompletableFuture<List<String>> future = hashGenerator.getHashesAsync();

        List<String> result = future.get();

        assertEquals(hashes, result);
    }
}
