package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.config.HashGeneratorProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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

    @BeforeEach
    public void setUp() {
        HashGeneratorProperties hashGeneratorProperties = new HashGeneratorProperties(10);
        hashGenerator = new HashGenerator(hashRepository, hashGeneratorProperties, base62Encoder);
    }

    @Test
    @DisplayName("Generate batch: success case")
    void testGenerateBatch_Success() {
        when(hashRepository.generateUniqueNumbers(any())).thenReturn(List.of(1L, 2L));
        when(base62Encoder.encode(any())).thenReturn(List.of(new Hash("1"), new Hash("2")));

        hashGenerator.generateBatch();

        verify(base62Encoder, times(1)).encode(any());
        verify(hashRepository, times(1)).generateUniqueNumbers(any());
        verify(hashRepository, times(1)).saveAll(any());
    }
}
