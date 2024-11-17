package faang.school.urlshortenerservice.service.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    private final int batchSize = 10;

    @BeforeEach
    public void setUp() throws Exception {
        Field batchSizeField = HashGenerator.class.getDeclaredField("batchSize");
        batchSizeField.setAccessible(true);
        batchSizeField.set(hashGenerator, batchSize);
    }

    @Test
    public void testGenerateHashes() {
        long amount = 5;
        List<Long> uniqueNumbers = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        List<String> encodedHashes = Arrays.asList("hash1", "hash2", "hash3", "hash4", "hash5");
        when(hashRepository.getUniqueNumbers(amount)).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(uniqueNumbers)).thenReturn(encodedHashes);

        List<String> result = hashGenerator.generateHashes(amount);

        assertEquals(encodedHashes, result);
        verify(hashRepository, times(1)).getUniqueNumbers(amount);
        verify(base62Encoder, times(1)).encode(uniqueNumbers);
    }

    @Test
    public void testGenerateHashesAsync() {
        List<String> hashes = Arrays.asList("hash1", "hash2");
        when(base62Encoder.encode(anyList())).thenReturn(hashes);
        when(hashRepository.getUniqueNumbers(batchSize)).thenReturn(Arrays.asList(1L, 2L));

        hashGenerator.generateHashesAsync();

        verify(hashRepository, times(1)).saveAll(anyList());
        verify(hashRepository, times(1)).getUniqueNumbers(batchSize);
        assertEquals(2, hashes.size());
    }

    @Test
    public void testGetHashes() {
        long amount = 5;
        List<String> existingHashes = new ArrayList<>(List.of("hash1", "hash2"));
        List<String> newHashes = Arrays.asList("hash3", "hash4", "hash5");
        when(hashRepository.findAndDelete(amount)).thenReturn(existingHashes);
        when(hashRepository.getUniqueNumbers(3)).thenReturn(Arrays.asList(3L, 4L, 5L));
        when(base62Encoder.encode(anyList())).thenReturn(newHashes);

        List<String> result = hashGenerator.getHashes(amount);

        assertEquals(5, result.size()); // Should return 5 hashes
        verify(hashRepository, times(1)).findAndDelete(amount);
        verify(hashRepository, times(1)).getUniqueNumbers(3);
    }

    @Test
    public void testGetHashesAsync() {
        long amount = 5;
        List<String> existingHashes = Arrays.asList("hash1", "hash2");
        when(hashRepository.findAndDelete(amount)).thenReturn(existingHashes);

        CompletableFuture<List<String>> result = hashGenerator.getHashesAsync(amount);

        assertEquals(existingHashes, result.join());
        verify(hashRepository, times(1)).findAndDelete(amount);
    }
}