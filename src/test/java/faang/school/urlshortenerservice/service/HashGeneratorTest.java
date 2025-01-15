package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.HashEntity;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {
    @InjectMocks
    private HashGenerator hashGenerator;

    @Mock
    private HashService hashService;

    @Mock
    private Base62Encoder base62Encoder;

    private int batchSize;

    @BeforeEach
    void setUp() {
        batchSize = 0;
    }

    @Test
    void testGenerateBatch() {
        List<Long> uniqueNumbers = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        List<HashEntity> encodedHashes = Arrays.asList(new HashEntity("a"), new HashEntity("b"), new HashEntity("c"), new HashEntity("d"), new HashEntity("e"));

        when(hashService.getUniqueNumbers(batchSize)).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(uniqueNumbers)).thenReturn(encodedHashes);

        hashGenerator.generateBatch(batchSize);

        verify(hashService).getUniqueNumbers(batchSize);
        verify(base62Encoder).encode(uniqueNumbers);
        verify(hashService).saveHashes(encodedHashes);
    }

    @Test
    void testGenerateBatchWithEmptyList() {
        List<Long> uniqueNumbers = Arrays.asList();
        List<HashEntity> encodedHashes = Arrays.asList();

        when(hashService.getUniqueNumbers(batchSize)).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(uniqueNumbers)).thenReturn(encodedHashes);

        hashGenerator.generateBatch(batchSize);

        verify(hashService).getUniqueNumbers(batchSize);
        verify(base62Encoder).encode(uniqueNumbers);
        verify(hashService).saveHashes(encodedHashes);
    }
}