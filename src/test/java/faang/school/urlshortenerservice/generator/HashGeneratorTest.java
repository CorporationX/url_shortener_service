package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {
    @Mock
    private HashRepository hashRepository;
    @InjectMocks
    private HashGenerator hashGenerator;

    @Test
    public void testGenerateHash() {
        List<Long> range = List.of(123L, 456L);
        when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(range);
        when(hashRepository.saveAll(any())).thenReturn(List.of(new Hash("aa"), new Hash("bb")));

        hashGenerator.generateHash();

        verify(hashRepository).getUniqueNumbers(anyInt());
        verify(hashRepository).saveAll(any());
    }

    @Test
    public void testGetHashes() {
        List<Hash> hashBatch = List.of(new Hash("hash1"), new Hash("hash2"));
        when(hashRepository.getHashBatch(anyLong())).thenReturn(hashBatch);

        List<Hash> hashes = hashGenerator.getHashes(2L);

        Assertions.assertNotNull(hashes);
        Assertions.assertEquals(2, hashes.size());
        verify(hashRepository, times(1)).getHashBatch(2L);
    }

    @Test
    public void testGetHashesInsufficient() {
        List<Hash> initialHashes = List.of(new Hash("hash1"));
        List<Hash> generatedHashes = List.of(new Hash("hash2"), new Hash("hash3"));
        when(hashRepository.getHashBatch(anyLong()))
                .thenReturn(initialHashes)
                .thenReturn(generatedHashes);
        List<Long> range = List.of(123L, 456L);
        when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(range);
        when(hashRepository.saveAll(any())).thenReturn(generatedHashes);

        List<Hash> hashes = hashGenerator.getHashes(3L);

        verify(hashRepository, times(2)).getHashBatch(anyLong());

        Assertions.assertNotNull(hashes);
        Assertions.assertEquals(3, hashes.size());
        List<Hash> expectedHashes = List.of(new Hash("hash1"), new Hash("hash2"), new Hash("hash3"));
        Assertions.assertTrue(hashes.containsAll(expectedHashes));
    }

    @Test
    public void testGetHashesAsync() throws Exception {
        List<Hash> hashBatch = List.of(new Hash("hash1"), new Hash("hash2"));
        when(hashRepository.getHashBatch(anyLong())).thenReturn(hashBatch);

        CompletableFuture<List<Hash>> futureHashes = hashGenerator.getHashesAsync(2L);

        Assertions.assertNotNull(futureHashes);
        List<Hash> hashes = futureHashes.get();
        Assertions.assertEquals(2, hashes.size());
        verify(hashRepository, times(1)).getHashBatch(2L);
    }
}
