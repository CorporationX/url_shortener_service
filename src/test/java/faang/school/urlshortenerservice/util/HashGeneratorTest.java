package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.model.util.Base62Encoder;
import faang.school.urlshortenerservice.model.util.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {

    @Mock
    private Base62Encoder encoder;
    @Mock
    private HashRepository hashRepository;
    @InjectMocks
    private HashGenerator hashGenerator;

    @Test
    void testGenerated_OK(){
        List<Long> sequenceNumbers = List.of(5L, 6L, 7L);
        List<String> hashes = List.of("asdW", "WEqw", "HHr2");
        when(hashRepository.getUniqueNumbers(anyLong())).thenReturn(sequenceNumbers);
        when(encoder.encode(sequenceNumbers)).thenReturn(hashes);

        hashGenerator.generateBatch();

        verify(hashRepository).save(hashes);
        verify(hashRepository).getUniqueNumbers(anyLong());
        verify(encoder).encode(sequenceNumbers);
    }
}
