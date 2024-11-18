package faang.school.urlshortenerservice.generate;

import faang.school.urlshortenerservice.exception.ServiceException;
import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {
    @InjectMocks
    private HashGenerator hashGenerator;
    @Mock
    private HashRepository hashRepository;
    @Mock
    private Base62Encoder base62Encoder;
    @Mock
    private ThreadPoolTaskExecutor asyncExecutor;

    private List<Long> numbers;
    private List<Hash> expectedHashes;
    private List<String> hashesList;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashGenerator, "maxRange", 10L);
        ReflectionTestUtils.setField(hashGenerator, "numberOfParts", 2);
        ReflectionTestUtils.setField(hashGenerator, "threshold", 10000);

        asyncExecutor = new ThreadPoolTaskExecutor();

        numbers = List.of(1L, 2L, 3L);
        expectedHashes = List.of(new Hash("hash1"), new Hash("hash2"), new Hash("hash3"));
        hashesList = List.of("hash1", "hash2", "hash3");
    }

    @Test
    void generateBatchSuccessForSmallBatch() {
        when(hashRepository.getUniqueNumbers(Mockito.anyLong())).thenReturn(numbers);
        when(base62Encoder.encode(anyList())).thenReturn(hashesList);

        hashGenerator.generateBatch();

        ArgumentCaptor<List<Hash>> hashCaptor = ArgumentCaptor.forClass(List.class);
        verify(hashRepository).saveAll(hashCaptor.capture());
        List<Hash> capturedHashes = hashCaptor.getValue();

        assertEquals(expectedHashes.size(), capturedHashes.size());
        assertEquals(expectedHashes.get(0), capturedHashes.get(0));

        verify(hashRepository, times(1)).getUniqueNumbers(Mockito.anyLong());
        verify(base62Encoder, times(1)).encode(anyList());
        verify(hashRepository, times(1)).saveAll(anyList());
    }

    @Test
    void generateBatchSuccessForLargeBatch() {
        when(hashRepository.getUniqueNumbers(Mockito.anyLong())).thenReturn(numbers);
        when(base62Encoder.encode(anyList())).thenReturn(hashesList);

        when(base62Encoder.encode(anyList())).thenAnswer(invocation -> {
            List<Long> sublist = invocation.getArgument(0);
            return sublist.stream()
                    .map(num -> "hash" + num)
                    .collect(Collectors.toList());
        });

        hashGenerator.generateBatch();

        ArgumentCaptor<List<Hash>> hashCaptor = ArgumentCaptor.forClass(List.class);
        verify(hashRepository).saveAll(hashCaptor.capture());
        List<Hash> capturedHashes = hashCaptor.getValue();

        assertEquals(expectedHashes.size(), capturedHashes.size());
        assertEquals(expectedHashes.get(0), capturedHashes.get(0));

        verify(hashRepository, times(1)).getUniqueNumbers(Mockito.anyLong());
        verify(base62Encoder, times(1)).encode(anyList());
        verify(hashRepository, times(1)).saveAll(anyList());

    }

    @Test
    void testGetHashesWithException() {
        when(base62Encoder.encode(anyList())).thenThrow(new ServiceException("Error receiving hashes", null));

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> hashGenerator.generateBatch()
        );

        assertTrue(exception.getMessage().contains("Error receiving hashes"));
    }
}