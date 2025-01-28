package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
public class HashGeneratorTest {
    @Mock
    private HashRepository hashRepository;
    @Mock
    private Base62Encoder base62Encoder;
    @InjectMocks
    private HashGenerator hashGenerator;
    List<Long> hashBatch = List.of(200L, 201L);
    private int batch = 2;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(hashGenerator, "batch", 2); // Set batch to 2
    }

    @Test
    @DisplayName("Generate and save hash batch")
    public void generateBatchTest() {
        List<Hash> encodedHashes = List.of(
                new Hash("Encoded200"),
                new Hash("Encoded201")
        );
        when(hashRepository.getUniqueNumbers(batch)).thenReturn(hashBatch);
        when(base62Encoder.encode(hashBatch)).thenReturn(encodedHashes);
        hashGenerator.generateBatch();

        verify(hashRepository).getUniqueNumbers(batch);
        verify(base62Encoder).encode(hashBatch);
        verify(hashRepository).saveAll(encodedHashes);
    }
}
