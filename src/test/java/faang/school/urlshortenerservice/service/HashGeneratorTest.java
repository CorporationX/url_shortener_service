package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.context.HashGeneratorConfig;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {
    @Mock
    private HashRepository hashRepository;
    @Mock
    private HashGeneratorConfig hashGeneratorConfig;
    @Mock
    private Base62Encoder base62Encoder;
    @InjectMocks
    private HashGenerator hashGenerator;
    @Test
    void generateBatch() {
        List<Long> numbers = Arrays.asList(1L, 2L, 3L, 4L, 5L);

        Mockito.when(hashRepository.getUniqueNumbers(5L))
                .thenReturn(numbers);
        Mockito.when(base62Encoder.encode(numbers))
                .thenReturn(Arrays.asList("a", "f", "s", "d", "c"));
        Mockito.when(hashGeneratorConfig.getUniqueBatch())
                .thenReturn(5);
        hashGenerator.generateBatch();

        Mockito.verify(hashRepository).getUniqueNumbers(5);
        Mockito.verify(base62Encoder).encode(numbers);
        Mockito.verify(hashRepository).save(anyList());
    }
}