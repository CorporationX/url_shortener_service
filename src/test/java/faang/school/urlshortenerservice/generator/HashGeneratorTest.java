package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.service.HashService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {

    @Mock
    HashService hashService;
    @Mock
    Base62Encoder encoder;
    @InjectMocks
    HashGenerator hashGenerator;

    @Test
    void testGenerateBatchSuccessful() {

        hashGenerator.generateBatch();

        verify(hashService, times(1)).getUniqueNumbers(anyInt());
        verify(encoder, times(1)).encode(List.of());
        verify(hashService, times(1)).saveAllHashes(List.of());
    }
}