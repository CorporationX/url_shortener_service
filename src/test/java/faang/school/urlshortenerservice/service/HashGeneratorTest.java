package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;
    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    @Test
    public void testGenerateBatch() {
    List<Long> numbers = Arrays.asList(1L,2L);
    Mockito.when(hashRepository.getUniqueNumbers(anyLong())).thenReturn(numbers);
    Mockito.when(base62Encoder.encode(eq(numbers))).thenReturn(Arrays.asList("a","b"));
    hashGenerator.generateBatch();

    Mockito.verify(hashRepository).saveAll(anyList());
    }
    


}