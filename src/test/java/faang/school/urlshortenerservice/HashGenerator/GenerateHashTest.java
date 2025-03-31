package faang.school.urlshortenerservice.HashGenerator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class GenerateHashTest {

    @InjectMocks
    private HashGenerator hashGenerator;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @Test
    public void testGenerateHash() {
        List<Long> numbers = List.of(1L, 2L, 3L);
        List<String> encodedNum = List.of("hash1", "hash2", "hash3");
        ArgumentCaptor<List<Hash>> hashCaptor = ArgumentCaptor.forClass(List.class);

        Mockito.when(hashRepository.getUniqueNumbers(anyLong())).thenReturn(numbers);
        Mockito.when(base62Encoder.encode(numbers)).thenReturn(encodedNum);

        hashGenerator.generateHash();

        Mockito.verify(hashRepository, Mockito.times(1)).getUniqueNumbers(anyLong());
        Mockito.verify(base62Encoder, Mockito.times(1)).encode(numbers);
        Mockito.verify(hashRepository, Mockito.times(1)).saveAll(hashCaptor.capture());
    }
}
