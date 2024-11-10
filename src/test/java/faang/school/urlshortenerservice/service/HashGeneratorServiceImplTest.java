package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UniqueNumberSequenceRepository;
import faang.school.urlshortenerservice.util.encoder.Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorServiceImplTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private UniqueNumberSequenceRepository numberSequenceRepository;

    @Mock
    private Encoder<Long, Hash> encoder;

    @InjectMocks
    private HashGeneratorServiceImpl hashGeneratorService;

    private int generateBatchSize;

    @BeforeEach
    void setUp() {
        generateBatchSize = 10;
        ReflectionTestUtils.setField(hashGeneratorService, "generateBatchSize", generateBatchSize);
    }

    @Test
    void testGenerateBatch() {
        List<Long> sequenceValues = List.of(1L, 2L, 3L);
        List<Hash> encodedHashes = List.of(new Hash("hash1"), new Hash("hash2"), new Hash("hash3"));

        when(numberSequenceRepository.getNextSequenceValues(generateBatchSize)).thenReturn(sequenceValues);
        when(encoder.encode(sequenceValues)).thenReturn(encodedHashes);

        hashGeneratorService.generateBatch();

        verify(numberSequenceRepository).getNextSequenceValues(generateBatchSize);
        verify(encoder).encode(sequenceValues);
        verify(hashRepository).saveAll(encodedHashes);
    }
}
