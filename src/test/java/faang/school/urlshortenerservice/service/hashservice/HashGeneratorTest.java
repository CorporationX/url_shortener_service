package faang.school.urlshortenerservice.service.hashservice;

import faang.school.urlshortenerservice.hashservice.Base62Encoder;
import faang.school.urlshortenerservice.hashservice.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
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
}
