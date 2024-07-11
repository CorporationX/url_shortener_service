package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.encoder.Base62Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashServiceImplTest {

    @Mock
    private Base62Encoder encoder;

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private HashServiceImpl hashService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashService, "batchSize", 3);
    }

    @Test
    void generateHashes() throws ExecutionException, InterruptedException {
        List<Long> uniqueNumbers = List.of(1L, 2L, 3L);
        List<String> encodedSymbols = List.of("A", "B", "C");

        when(hashRepository.findUniqueNumbers(3)).thenReturn(uniqueNumbers);
        when(encoder.encodeSymbolsToHash(uniqueNumbers)).thenReturn(encodedSymbols);

        CompletableFuture<Void> result = hashService.generateHashes();

        result.join();

        assertNull(result.get());
    }

    @Test
    void getHashes() {
        List<String> hashes = List.of("A", "B", "C");

        when(hashRepository.getHashBatch(3)).thenReturn(hashes);

        List<String> result = hashService.getHashes(3);

        assertEquals(hashes, result);
    }

    @Test
    void getHashesAsync() throws ExecutionException, InterruptedException {
        List<String> hashes = List.of("A", "B", "C");

        when(hashRepository.getHashBatch(3)).thenReturn(hashes);

        CompletableFuture<List<String>> resultFuture = hashService.getHashesAsync(3);
        List<String> result = resultFuture.get();

        assertEquals(hashes, result);
    }
}
