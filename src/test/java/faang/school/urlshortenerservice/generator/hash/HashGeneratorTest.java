package faang.school.urlshortenerservice.generator.hash;

import faang.school.urlshortenerservice.service.hash.HashService;
import faang.school.urlshortenerservice.service.uniquenumber.UniqueNumber;
import io.seruco.encoding.base62.Base62;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {
    @InjectMocks
    public HashGenerator hashGenerator;

    @Mock
    public HashService hashService;

    @Mock
    public Base62Encoder base62Encoder;

    @Mock
    public UniqueNumber uniqueNumber;

    private int quantity;
    private List<Long> numbers;
    private List<String> hash;

    @BeforeEach
    public void setUp() {
        quantity = 3;
        hashGenerator.setQuantity(3);
        numbers = new ArrayList<>(List.of(1L, 2L, 3L));
        hash = new ArrayList<>(List.of("n", "o", "p"));
    }

    @Test()
    public void generateBatchTest() {
        when(uniqueNumber.getUniqueNumbers(quantity)).thenReturn(numbers);
        when(base62Encoder.encode(numbers)).thenReturn(hash);

        hashGenerator.generateBatch();

        verify(hashService, times(1)).saveHashes(hash);
    }
}

