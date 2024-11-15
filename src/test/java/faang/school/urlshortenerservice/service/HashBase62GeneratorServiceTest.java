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
public class HashBase62GeneratorServiceTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private UniqueNumberSequenceRepository numberSequenceRepository;

    @Mock
    private Encoder<Long, Hash> encoder;

    @InjectMocks
    private HashBase62GeneratorService hashGeneratorService;

    private int generateBatchSize;

    @BeforeEach
    void setUp() {
        generateBatchSize = 10;
        ReflectionTestUtils.setField(hashGeneratorService, "batchSizeForGenerateFreeHashes", generateBatchSize);
    }

    @Test
    void testGenerateFreeHashes() {
        List<Long> sequenceValues = List.of(1L, 2L, 3L);
        List<Hash> encodedHashes = List.of(new Hash("hash1"), new Hash("hash2"), new Hash("hash3"));

        when(numberSequenceRepository.getUniqueNumbers(generateBatchSize)).thenReturn(sequenceValues);
        when(encoder.encode(sequenceValues)).thenReturn(encodedHashes);

        hashGeneratorService.generateFreeHashes();

        verify(numberSequenceRepository).getUniqueNumbers(generateBatchSize);
        verify(encoder).encode(sequenceValues);
        verify(hashRepository).saveAll(encodedHashes);
    }
}
