package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.db.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {
    @InjectMocks
    private HashGenerator hashGenerator;

    @Mock
    private HashRepository hashRepository;
    @Mock
    private Base62Encoder base62Encoder;

    private final int batchSize = 5000;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashGenerator, "batchSize", batchSize);
    }


    @Test
    void test_generateBatchIfNeeded() {
        List<Long> uniqueNumbers = List.of(1L, 2L);
        List<String> hashes = List.of("1", "2");
        when(hashRepository.getUniqueNumbers(batchSize)).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(uniqueNumbers)).thenReturn(hashes);

        hashGenerator.generateBatchIfNeeded();

        verify(hashRepository, times(1)).getHashesNumber();
        verify(hashRepository, times(1)).getUniqueNumbers(batchSize);
        verify(base62Encoder, times(1)).encode(uniqueNumbers);
        verify(hashRepository, times(1)).saveBatch(hashes);
        verifyNoMoreInteractions(hashRepository, base62Encoder);
    }

    @Test
    void test_generateBatchIfNeeded_notNeeded_doNothing() {
        when(hashRepository.getHashesNumber()).thenReturn(5000);

        hashGenerator.generateBatchIfNeeded();
        verify(hashRepository, times(1)).getHashesNumber();
        verifyNoMoreInteractions(hashRepository, base62Encoder);
    }

    @Test
    void test_generateBatchIfNeededAsync() {
        test_generateBatchIfNeeded();
    }

    @Test
    void getHashes() {
        int numberToRefill = 2;
        List<String> hashes = List.of("1", "2");
        when(hashRepository.pollHashBatch(numberToRefill)).thenReturn(hashes);

        List<String> result = hashGenerator.getHashes(numberToRefill);

        assertEquals(hashes, result);
        Mockito.verify(hashRepository, Mockito.times(1))
                .pollHashBatch(numberToRefill);
    }
}