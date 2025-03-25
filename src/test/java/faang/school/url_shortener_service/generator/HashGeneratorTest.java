package faang.school.url_shortener_service.generator;


import faang.school.url_shortener_service.entity.Hash;
import faang.school.url_shortener_service.repository.hash.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        ReflectionTestUtils.setField(hashGenerator, "hashBatchSize", 5);
    }

    @Test
    void testGenerateBatch_shouldGenerateAndSaveHashes() {
        List<Long> uniqueNumbers = List.of(1L, 2L, 3L, 4L, 5L);
        List<Hash> hashes = uniqueNumbers.stream()
                .map(n -> new Hash("h" + n))
                .toList();

        when(hashRepository.getUniqueNumbers(5)).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(uniqueNumbers)).thenReturn(hashes);

        hashGenerator.generateBatch();

        verify(hashRepository).saveAll(hashes);
    }

    @Test
    void testGetHashes_shouldReturnHashesWhenAvailable() {
        List<Hash> dbHashes = List.of(new Hash("a"), new Hash("b"), new Hash("c"));

        when(hashRepository.getHashBatch(3)).thenReturn(dbHashes);

        List<String> result = hashGenerator.getHashes(3);

        assertEquals(List.of("a", "b", "c"), result);
    }

    @Test
    void testGenerateHashesAsync_shouldReturnCompletableFuture() throws Exception {
        List<Hash> dbHashes = List.of(new Hash("x"), new Hash("y"));

        when(hashRepository.getHashBatch(2)).thenReturn(dbHashes);

        CompletableFuture<List<String>> future = hashGenerator.generateHashesAsync(2);

        assertTrue(future.isDone());
        assertEquals(List.of("x", "y"), future.get());
    }

    @Test
    void testSaveHashesToDb_shouldConvertAndSave() {
        List<String> hashes = List.of("aa", "bb");
        List<Hash> hashEntities = List.of(new Hash("aa"), new Hash("bb"));

        hashGenerator.saveHashesToDb(hashes);

        verify(hashRepository).saveAll(hashEntities);
    }
}