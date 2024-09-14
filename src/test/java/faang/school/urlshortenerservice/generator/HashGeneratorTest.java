package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.Base62;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void getHashBatch_ShouldReturnSufficientHashes() {
        List<Hash> hashBatch = Arrays.asList(new Hash("hash1"), new Hash("hash2"), new Hash("hash3"));
        when(hashRepository.getAndDeleteHashBatch(3)).thenReturn(hashBatch);

        List<String> result = hashGenerator.getHashBatch(3);

        assertEquals(3, result.size());
        assertEquals("hash1", result.get(0));
        assertEquals("hash2", result.get(1));
        assertEquals("hash3", result.get(2));

        verify(hashRepository, times(1)).getAndDeleteHashBatch(3);
        verify(hashRepository, never()).getNextRange(anyInt());
    }

    @Test
    void getHashBatch_ShouldGenerateMoreHashesIfNotEnough() {
        List<Hash> initialBatch = Arrays.asList(new Hash("hash1"));
        when(hashRepository.getAndDeleteHashBatch(3)).thenReturn(initialBatch);

        List<Long> numbers = Arrays.asList(2L, 3L);
        when(hashRepository.getNextRange(anyInt())).thenReturn(numbers);
        when(base62.encode(2L)).thenReturn("hash2");
        when(base62.encode(3L)).thenReturn("hash3");

        List<Hash> additionalBatch = Arrays.asList(new Hash("hash2"), new Hash("hash3"));
        when(hashRepository.getAndDeleteHashBatch(2)).thenReturn(additionalBatch);

        List<String> result = hashGenerator.getHashBatch(3);

        assertEquals(3, result.size());
        assertEquals("hash1", result.get(0));
        assertEquals("hash2", result.get(1));
        assertEquals("hash3", result.get(2));

        verify(hashRepository, times(1)).getAndDeleteHashBatch(3);
        verify(hashRepository, times(1)).getNextRange(anyInt());
        verify(hashRepository, times(1)).getAndDeleteHashBatch(2);
    }
}