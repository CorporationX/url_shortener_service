package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.properties.HashGeneratorProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @Mock
    private HashGeneratorProperties hashGeneratorProperties;

    @InjectMocks
    private HashGenerator hashGenerator;

    @Test
    void getHashesTest() {
        int amount = 3;
        List<Hash> hashes = List.of(new Hash("hash1"), new Hash("hash2"), new Hash("hash3"));
        when(hashRepository.getAndDelete(amount)).thenReturn(hashes);

        List<String> result = hashGenerator.getHashes(amount);

        assertEquals(List.of("hash1", "hash2", "hash3"), result);
    }

    @Test
    void isBelowMinimumTrueTest() {
        int currentCount = 50;
        int minLimit = 100;
        when(hashRepository.getHashesSize()).thenReturn(currentCount);
        when(hashGeneratorProperties.getMinLimit()).thenReturn(minLimit);

        boolean result = hashGenerator.isBelowMinimum();

        assertEquals(true, result);
    }

    @Test
    void isBelowMinimumFalseTest() {
        int currentCount = 150;
        int minLimit = 100;
        when(hashRepository.getHashesSize()).thenReturn(currentCount);
        when(hashGeneratorProperties.getMinLimit()).thenReturn(minLimit);

        boolean result = hashGenerator.isBelowMinimum();

        assertEquals(false, result);
    }
}
