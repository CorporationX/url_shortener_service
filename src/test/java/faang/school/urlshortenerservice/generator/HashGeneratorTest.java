package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.Base62;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62 base62;

    @InjectMocks
    private HashGenerator hashGenerator;

    @Test
    void generateHashBatch_ShouldGenerateAndSaveHashes() {
        List<Long> numbers = Arrays.asList(1L, 2L, 3L);
        when(hashRepository.getNextRange(anyInt())).thenReturn(numbers);
        when(base62.encode(1L)).thenReturn("1");
        when(base62.encode(2L)).thenReturn("2");
        when(base62.encode(3L)).thenReturn("3");

        hashGenerator.generateHashBatch();

        verify(hashRepository, times(1)).saveAll(anyList());
    }
}