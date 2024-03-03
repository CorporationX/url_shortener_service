package faang.school.urlshortenerservice.generator;

import static org.mockito.Mockito.*;

import faang.school.urlshortenerservice.base62encoder.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {
    @InjectMocks
    private HashGenerator hashGenerator;
    @Mock
    private HashRepository hashRepository;
    @Mock
    private Base62Encoder base62Encoder;
    int range = 10000;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(hashGenerator, "range", range);
    }

    @Test
    void testBase62Encoder() {
        List<Long> expectedList = List.of(1L, 2L);
        when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(expectedList);
        hashGenerator.generateBatch();
        verify(base62Encoder, times(1)).encodeList(expectedList);
    }

    @Test
    void testGetHashes() {
        hashGenerator.getHashes(5);
        verify(hashRepository, times(2)).getHashBatch(5);

    }
}
