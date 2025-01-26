package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.service.HashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {

    @Mock
    private HashService hashService;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    @Value("${hash.range:1000}")
    private int range;

    @BeforeEach
    public void setUp() {
        hashGenerator = new HashGenerator(hashService, base62Encoder);
    }

    @Test
    public void testGenerateBatch() {
        List<Long> uniqueNumbers = List.of(1L, 2L, 3L);
        List<String> encodedNumbers = List.of("a", "b", "c");

        when(hashService.getUniqueNumbers(range)).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(uniqueNumbers)).thenReturn(encodedNumbers);

        hashGenerator.generateBatch();

        verify(hashService, times(1)).getUniqueNumbers(range);
        verify(base62Encoder, times(1)).encode(uniqueNumbers);
        verify(hashService, times(1)).saveHashes(anyList());
    }

}
