package faang.school.urlshortenerservice.generator;

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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {
    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private HashGenerator hashGenerator;

    @Test
    public void testGetHashWhenAmountIsLess() {
        long amount = 5;
        List<Hash> hashes = Arrays.asList(new Hash("hash1"), new Hash("hash2"), new Hash("hash3"), new Hash("hash4"), new Hash("hash5"));
        when(hashRepository.findAndDelete(amount)).thenReturn(hashes);

        List<String> result = hashGenerator.getHash(amount);

        assertEquals(amount, result.size());
        verify(hashRepository, times(1)).findAndDelete(amount);
    }
}