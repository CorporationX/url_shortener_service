package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.managers.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.HashJdbcRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashJdbcRepository hashJdbcRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    private final int hashBatchSize = 3;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        setPrivateField(hashGenerator, "maxRange", 10);
        setPrivateField(hashGenerator, "hashButchSize", hashBatchSize);
    }

    private void setPrivateField(Object object, String fieldName, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    @Test
    public void testGenerateBatch() {
        List<Long> uniqueNumbers = Arrays.asList(1L, 2L, 3L);
        List<String> encodedHashes = Arrays.asList("a", "b", "c");

        when(hashRepository.getUniqueNumbers(anyLong())).thenReturn(List.of(1L, 2L, 3L));
        when(base62Encoder.encode(uniqueNumbers)).thenReturn(encodedHashes);

        hashGenerator.generateBatch();

        verify(hashRepository, times(1)).getUniqueNumbers(anyLong());
        verify(base62Encoder, times(1)).encode(uniqueNumbers);
        verify(hashJdbcRepository, times(1)).saveBatch(encodedHashes);
    }

    @Test
    public void testGetHashes() throws Exception {
        List<Hash> hashBatch = Arrays.asList(new Hash("a"), new Hash("b"), new Hash("c"));

        when(hashRepository.getHashBatch(hashBatchSize)).thenReturn(hashBatch);

        CompletableFuture<List<Hash>> future = hashGenerator.getHashes();

        List<Hash> result = future.get();
        assertEquals(hashBatch.size(), result.size());
        verify(hashRepository, times(1)).getHashBatch(hashBatchSize);
    }

    @Test
    public void testGetHashesWhenEmpty() throws Exception {
        HashGenerator spyHashGenerator = spy(hashGenerator); // Создаем spy-объект

        List<Hash> emptyList = new ArrayList<>();
        List<Hash> generatedHashes = Arrays.asList(
                Hash.builder().hash("x").build(),
                Hash.builder().hash("y").build(),
                Hash.builder().hash("z").build()
        );

        when(hashRepository.getHashBatch(hashBatchSize))
                .thenReturn(emptyList)
                .thenReturn(generatedHashes);

        doNothing().when(spyHashGenerator).generateBatch();

        CompletableFuture<List<Hash>> future = spyHashGenerator.getHashes();
        List<Hash> result = future.get();

        assertEquals(generatedHashes.size(), result.size());
        verify(hashRepository, times(2)).getHashBatch(hashBatchSize);
        verify(spyHashGenerator, times(1)).generateBatch();
    }

    @Test
    public void testGetHashesSync() {
        List<Hash> hashBatch = Arrays.asList(new Hash("a"), new Hash("b"), new Hash("c"));

        when(hashRepository.getHashBatch(hashBatchSize)).thenReturn(hashBatch);

        List<Hash> result = hashGenerator.getHashesSync();

        assertEquals(hashBatch.size(), result.size());
        verify(hashRepository, times(1)).getHashBatch(hashBatchSize);
    }
}
