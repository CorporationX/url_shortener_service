package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.managers.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTests {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    @BeforeEach
    public void setUp() {
        // Можно использовать для предварительной настройки перед каждым тестом
    }

    @Test
    public void testGenerateBatch() {
        List<Long> uniqueNumbers = Arrays.asList(1L, 2L, 3L);
        List<String> encodedHashes = Arrays.asList("a", "b", "c");

        when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(uniqueNumbers)).thenReturn(encodedHashes);

        hashGenerator.generateBatch();

        verify(hashRepository, times(1)).getUniqueNumbers(anyInt());
        verify(base62Encoder, times(1)).encode(uniqueNumbers);
        verify(hashRepository, times(1)).saveBatch(encodedHashes);
    }

    @Test
    public void testGetHashBatch() throws Exception {
        List<Hash> hashBatch = Arrays.asList(new Hash(), new Hash());

        when(hashRepository.getHashBatch(anyInt())).thenReturn(hashBatch);

        CompletableFuture<List<Hash>> future = hashGenerator.getHashBatch();

        List<Hash> result = future.get();
        assertEquals(hashBatch.size(), result.size());
        verify(hashRepository, times(1)).getHashBatch(anyInt());
    }
}
