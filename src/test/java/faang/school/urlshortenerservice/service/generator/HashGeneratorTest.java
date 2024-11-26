package faang.school.urlshortenerservice.service.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;

    @Spy
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    private final int minCacheSizeAtDB = 10;
    private List<Long> numbers;
    private List<String> strHashes;
    private List<Hash> hashes;

    @BeforeEach
    public void setUp() throws Exception {
        Field batchSizeField = HashGenerator.class.getDeclaredField("minCacheSizeAtDB");
        batchSizeField.setAccessible(true);
        batchSizeField.set(hashGenerator, minCacheSizeAtDB);

        Field minLoadFactorField = HashGenerator.class.getDeclaredField("minLoadFactor");
        minLoadFactorField.setAccessible(true);
        int loadFactor = 80;
        minLoadFactorField.set(hashGenerator, loadFactor);

        numbers = LongStream.rangeClosed(1_000_000_001L, 1_000_000_010L)
                .boxed()
                .toList();
        strHashes = Arrays.asList("Hgtf51", "Igtf51", "Jgtf51", "Kgtf51", "Lgtf51",
                "Mgtf51", "Ngtf51", "Ogtf51", "Pgtf51", "Qgtf51");
        hashes = strHashes.stream()
                .map(Hash::new)
                .toList();
    }

    @Test
    public void testGenerateHashes() {
        long amount = 10;
        when(hashRepository.getUniqueNumbers(amount)).thenReturn(numbers);

        List<String> result = hashGenerator.generateHashes(amount);

        assertEquals(strHashes, result);
        verify(hashRepository, times(1)).getUniqueNumbers(amount);
    }

    @Test
    public void testGenerateHashesAsyncWhenEnoughHashes() {
        when(hashRepository.count()).thenReturn(9L);

        hashGenerator.generateHashesAsync();

        verify(hashRepository, times(0)).saveAll(anyList());
        verify(hashRepository, times(0)).getUniqueNumbers(anyLong());
    }

    @Test
    public void testGenerateHashesAsync() {
        when(hashRepository.count()).thenReturn(0L);
        when(hashRepository.getUniqueNumbers(minCacheSizeAtDB)).thenReturn(numbers);

        hashGenerator.generateHashesAsync();

        verify(hashRepository, times(1)).saveAll(hashes);
        verify(hashRepository, times(1)).getUniqueNumbers(minCacheSizeAtDB);
    }

    @Test
    public void testGetHashes() {
        long amount = 5;
        List<String> existingHashes = new ArrayList<>(List.of("hash1", "hash2", "hash3", "hash4", "hash5"));
        when(hashRepository.findAndDelete(amount)).thenReturn(existingHashes);

        List<String> result = hashGenerator.getHashes(amount);

        assertEquals(5, result.size());
        verify(hashRepository, times(1)).findAndDelete(amount);
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