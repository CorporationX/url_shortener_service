package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    private static final int MAX_RANGE = 1000;
    private static final int BATCH_SIZE = 10;
    private static final int HASH_BATCH_SIZE = 3;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashGenerator, "maxRange", MAX_RANGE);
        ReflectionTestUtils.setField(hashGenerator, "hashBatchSize", BATCH_SIZE);
    }

    @Test
    void TestGenerateBatch_GenerateAndSaveHashes() {
        when(hashRepository.getUniqueNumbers(MAX_RANGE)).thenReturn(List.of(1L, 2L, 3L));
        when(base62Encoder.encode(List.of(1L, 2L, 3L))).thenReturn(List.of("A", "B", "C"));

        hashGenerator.generateBatch();

        verify(hashRepository).saveAll(List.of(new Hash("A"), new Hash("B"), new Hash("C")));
    }

    @Test
    void TestGetHashes_ReturnsExistingHashes() {
        HashRepository mockHashRepository = mock(HashRepository.class);
        Base62Encoder mockBase62Encoder = mock(Base62Encoder.class);

        HashGenerator hashGenerator = new HashGenerator(mockHashRepository, mockBase62Encoder);
        ReflectionTestUtils.setField(hashGenerator, "hashBatchSize", HASH_BATCH_SIZE);

        List<Hash> existingHashes = Arrays.asList(
                new Hash("hash1"),
                new Hash("hash2"),
                new Hash("hash3")
        );
        when(mockHashRepository.getHashBatch(HASH_BATCH_SIZE)).thenReturn(existingHashes);

        List<Hash> result = hashGenerator.getHashes();

        assertEquals(existingHashes.size(), result.size());
        assertTrue(result.containsAll(existingHashes));
        verify(mockHashRepository, times(1)).getHashBatch(HASH_BATCH_SIZE);
    }

    @Test
    void TestGetHashes_GenerateNewBatchWhenNotEnoughHashes() {
        List<Hash> existingHashes = List.of(new Hash("X"));
        List<Long> uniqueNumbers = List.of(4L, 5L);
        List<String> encodedHashes = List.of("D", "E");
        List<Hash> newHashes = List.of(new Hash("D"), new Hash("E"));

        when(hashRepository.getHashBatch(BATCH_SIZE)).thenReturn(existingHashes)
                .thenReturn(newHashes);
        when(hashRepository.getUniqueNumbers(MAX_RANGE)).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(uniqueNumbers)).thenReturn(encodedHashes);

        List<Hash> result = hashGenerator.getHashes();

        assertTrue(result.containsAll(existingHashes) && result.containsAll(newHashes));

        verify(hashRepository, times(2)).getHashBatch(BATCH_SIZE);
        verify(hashRepository, times(1)).saveAll(newHashes);
    }
}