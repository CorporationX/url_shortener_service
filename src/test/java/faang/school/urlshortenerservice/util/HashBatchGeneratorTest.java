package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashBatchGeneratorTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashBatchGenerator hashBatchGenerator;

    @Test
    void testGenerateBatch_ShouldSaveEncodedHashes() {
        ReflectionTestUtils.setField(hashBatchGenerator, "maxRange", 3);
        List<Long> inputNumbers = List.of(1L, 2L, 3L);
        List<String> encodedHashes = List.of("a", "b", "c");

        when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(inputNumbers);
        when(base62Encoder.encode(inputNumbers)).thenReturn(encodedHashes);

        hashBatchGenerator.generateBatch();

        verify(hashRepository).getUniqueNumbers(3);
        verify(base62Encoder).encode(inputNumbers);
        verify(hashRepository).save(encodedHashes);
    }

    @Test
    void testGenerateBatch_ShouldRunAsynchronously() throws Exception {
        when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(List.of(1L));
        when(base62Encoder.encode(anyList())).thenReturn(List.of("a"));

        Future<?> result = new SimpleAsyncTaskExecutor()
                .submit(() -> hashBatchGenerator.generateBatch());

        assertNull(result.get());
    }

    @Test
    void testGenerateBatch_ShouldHandleEmptyRange() {
        when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(List.of());

        hashBatchGenerator.generateBatch();

        verify(hashRepository).save(List.of());
    }
}