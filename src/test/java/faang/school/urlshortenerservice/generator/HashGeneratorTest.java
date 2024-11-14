package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UniqueIdRepository;
import faang.school.urlshortenerservice.util.encoder.Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {
    @Mock
    private HashRepository hashRepository;
    @Mock
    private UniqueIdRepository uniqueIdRepository;
    @Mock
    private Encoder<Long, Hash> encoder;
    @InjectMocks
    private HashGenerator hashGenerator;

    private int generateBatchSize;

    @BeforeEach
    void setup() {
        generateBatchSize = 10;
        ReflectionTestUtils.setField(hashGenerator, "generateBatchSize", generateBatchSize);
    }

    @Test
    void testGenerateBatch() {
        List<Long> uniqueNumbers = List.of(1L, 2L, 3L);
        List<Hash> hashes = List.of(new Hash("hash1"), new Hash("hash2"), new Hash("hash3"));

        when(uniqueIdRepository.getUniqueNumbers(generateBatchSize)).thenReturn(uniqueNumbers);
        when(encoder.encode(uniqueNumbers)).thenReturn(hashes);

        hashGenerator.generateBatch();

        verify(uniqueIdRepository, times(1)).getUniqueNumbers(generateBatchSize);
        verify(encoder, times(1)).encode(uniqueNumbers);
        verify(hashRepository, times(1)).saveAll(hashes);
    }
}
