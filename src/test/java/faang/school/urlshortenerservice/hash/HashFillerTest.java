package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashFillerTest {

    private static final int BATCH_SIZE = 3;

    @InjectMocks
    private HashFiller hashFiller;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashGenerator hashGenerator;

    @Captor
    ArgumentCaptor<List<String>> hashCaptor;
    private List<Hash> hashes;
    private List<String> stringHashes;


    @BeforeEach
    public void setup() {
        hashes = new ArrayList<>(
            Arrays.asList(
                createHash("123"),
                createHash("dAsO"),
                createHash("hgb")
            )
        );
        stringHashes = new ArrayList<>(
            Arrays.asList(
                "123",
                "dAsO",
                "hgb"
            )
        );
    }

    @Test
    public void testFillHashCache_NotHash() throws ExecutionException, InterruptedException {
        // Arrange
        when(hashRepository.deleteByIdsAndGet(BATCH_SIZE)).thenReturn(new ArrayList<>());
        when(hashGenerator.getStringHashes()).thenReturn(new ArrayList<>(List.of("123")));
        List<String> expectedHashes = new ArrayList<>(List.of("123"));

        // Act
        CompletableFuture<List<String>> hashes = hashFiller.fillHashCache(BATCH_SIZE);

        // Assert
        verify(hashGenerator, times(1)).getStringHashes();
        assertEquals(expectedHashes, hashes.get());

    }

    @Test
    public void testFillHashCache_Success() throws ExecutionException, InterruptedException {
        // Arrange
        when(hashRepository.deleteByIdsAndGet(BATCH_SIZE)).thenReturn(hashes);

        // Act
        CompletableFuture<List<String>> future = hashFiller.fillHashCache(BATCH_SIZE);

        // Assert
        verify(hashGenerator, times(1)).generateBatch();
        assertEquals(stringHashes, future.get());
    }

    private Hash createHash(String hash) {
        return Hash.builder().hash(hash).build();
    }
}
