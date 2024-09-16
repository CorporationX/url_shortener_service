package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.generator.Base62Encoder;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestHashGenerator {
    @Mock
    private HashRepository hashRepository;
    @Mock
    private Base62Encoder base62Encoder;
    @InjectMocks
    private HashGenerator hashGenerator;

    @Test
    public void testGenerateHash() {
        List<Long> numbers = Arrays.asList(1L, 2L, 3L);
        List<String> hashes = Arrays.asList("a", "b", "c");
        when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(numbers);
        when(base62Encoder.encode(anyList())).thenReturn(hashes);
        hashGenerator.generateBatch();
        verify(hashRepository, times(1)).saveHashes(hashes);
    }

    @Test
    public void testGetHashes() {
        int amount = 3;
        List<String> hashes = Arrays.asList("a", "b", "c");
        when(hashRepository.getAndDeleteHashes(anyInt())).thenReturn(hashes);
        List<String> result = hashGenerator.getHashes(3);
        assertEquals("a", result.get(0));
        assertEquals("b", result.get(1));
        assertEquals("c", result.get(2));
    }

    @Test
    public void testGetHashesNotEnough() {
        int amount = 5;
        List<String> hashes = new ArrayList<>(Arrays.asList("a", "b", "c"));
        List<String> additionalHashes = Arrays.asList("d", "e");
        when(hashRepository.getAndDeleteHashes(amount)).thenReturn(hashes);
        when(hashRepository.getAndDeleteHashes(amount - 3)).thenReturn(additionalHashes);
        when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(Arrays.asList(1L, 2L, 3L));
        when(base62Encoder.encode(anyList())).thenReturn(Arrays.asList("f", "g", "h"));
        List<String> result = hashGenerator.getHashes(amount);
        assertEquals(5, result.size());
        assertEquals("a", result.get(0));
        assertEquals("b", result.get(1));
        assertEquals("c", result.get(2));
        assertEquals("d", result.get(3));
        assertEquals("e", result.get(4));
    }
}
