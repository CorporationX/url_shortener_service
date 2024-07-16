package faang.school.urlshortenerservice.service.hashservice;

import faang.school.urlshortenerservice.hashservice.Base62Encoder;
import faang.school.urlshortenerservice.hashservice.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
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

    @InjectMocks
    HashGenerator hashGenerator;

    @Mock
    HashRepository hashRepository;

    @Mock
    Base62Encoder base62Encoder;

    @Value("${hash.quantity-numbers}")
    private int quantityNumbers;

    private List<String> hashes;
    private long quantity;

    @BeforeEach
    public void setUp() {
        hashes = List.of("fdbg3d", "wdfsd1", "scadsd");
        quantity = 3L;
    }

    @Test
    public void testGenerateBatch() {
        List<Long> uniqueNumbers = List.of(1L);
        List<String> hashes = List.of("000001");

        when(hashRepository.getUniqueNumbers(quantityNumbers)).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(uniqueNumbers)).thenReturn(hashes);

        hashGenerator.generateBatch();

        verify(hashRepository, times(1)).getUniqueNumbers(quantityNumbers);
        verify(base62Encoder, times(1)).encode(uniqueNumbers);
        verify(hashRepository, times(1)).save(hashes);
    }

    @Test
    public void testGetHashes() {
        when(hashRepository.getHashesSize()).thenReturn(5L);
        when(hashRepository.getHashBatch(quantity)).thenReturn(hashes);
        hashGenerator.getHashes(quantity);

        verify(hashRepository, times(1)).getHashesSize();
        verify(hashRepository, times(1)).getHashBatch(quantity);
    }

    @Test
    public void testGetHashesAsync() {
        when(hashRepository.getHashesSize()).thenReturn(quantity);
        when(hashRepository.getHashBatch(quantity)).thenReturn(hashes);

        hashGenerator.getHashesAsync(quantity);

        verify(hashRepository, times(1)).getHashesSize();
        verify(hashRepository, times(1)).getHashBatch(quantity);
    }
}
