package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.entity.hash.Hash;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.repository.sequence.UniqueNumberSequenceRepository;
import faang.school.urlshortenerservice.service.base62encoder.Base62Encoder;
import faang.school.urlshortenerservice.service.hash.HashGeneratorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorImplTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @Mock
    private UniqueNumberSequenceRepository uniqueNumberSequenceRepository;

    @InjectMocks
    private HashGeneratorImpl hashGenerator;
    private static final int BATCH_SIZE = 50;
    private static final int BATCH_PARTITION = 5;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashGenerator, "BATCH_SIZE", BATCH_SIZE);
        ReflectionTestUtils.setField(hashGenerator, "BATCH_PARTITION", BATCH_PARTITION);
    }

    @Test
    void generateBatchSuccessfully() {
        List<Long> numbers = List.of(1L, 2L, 3L, 4L, 5L);
        List<String> encodedNumbers = List.of("a", "b", "c", "d", "e");
        List<Hash> hashes = List.of(new Hash("a"), new Hash("b"), new Hash("c"), new Hash("d"), new Hash("e"));

        when(uniqueNumberSequenceRepository.getUniqueNumbers(anyInt())).thenReturn(numbers);
        when(base62Encoder.encodeListNumbers(anyList())).thenReturn(encodedNumbers);
        when(hashRepository.saveAll(anyList())).thenReturn(hashes);

        CompletableFuture<List<Hash>> result = hashGenerator.generateBatch();

        assertTrue(result.isDone());
        assertEquals(hashes, result.join());
        verify(uniqueNumberSequenceRepository).getUniqueNumbers(anyInt());
        verify(base62Encoder).encodeListNumbers(anyList());
        verify(hashRepository).saveAll(anyList());
    }

    @Test
    void generateBatchWithEmptyNumbers() {
        List<Long> numbers = List.of();

        when(uniqueNumberSequenceRepository.getUniqueNumbers(anyInt())).thenReturn(numbers);

        assertThrows(RuntimeException.class, () -> hashGenerator.generateBatch());

        verify(uniqueNumberSequenceRepository).getUniqueNumbers(anyInt());
    }

    @Test
    void generateBatchWithException() {
        when(uniqueNumberSequenceRepository.getUniqueNumbers(anyInt())).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> hashGenerator.generateBatch());

        verify(uniqueNumberSequenceRepository).getUniqueNumbers(anyInt());
    }
}
