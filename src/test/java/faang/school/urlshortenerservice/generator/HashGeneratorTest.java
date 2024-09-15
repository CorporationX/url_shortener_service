package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
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
    private Base62Encoder base62;

    @InjectMocks
    private HashGenerator hashGenerator;

    @Test
    void generateHashBatchTest() {
        List<Long> numbers = Arrays.asList(1L, 2L, 3L);
        when(hashRepository.getUniqueNumbers(anyLong())).thenReturn(numbers);
        when(base62.encode(List.of(1L, 2L, 3L))).thenReturn(List.of(new Hash("1"), new Hash("2"), new Hash("3")));

        hashGenerator.generateBatch();

        verify(hashRepository, times(1)).saveAll(anyList());
    }

    @Test
    void getHashBatchShouldReturnSufficientHashes() {
        List<Hash> hashBatch = Arrays.asList(new Hash("hash1"), new Hash("hash2"), new Hash("hash3"));
        when(hashRepository.getHashBatchAndDelete(3)).thenReturn(hashBatch);

        List<Hash> result = hashGenerator.getHashes(3);

        assertEquals(3, result.size());
        assertEquals("hash1", result.get(0).getHash());
        assertEquals("hash2", result.get(1).getHash());
        assertEquals("hash3", result.get(2).getHash());

        verify(hashRepository, times(1)).getHashBatchAndDelete(3);
    }
}
