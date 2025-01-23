package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;
    @Mock
    private Base62Encoder base62Encoder;
    @InjectMocks
    private HashGenerator hashGenerator;
    private long maxRange;

    @BeforeEach
    public void setUp() {
        maxRange = 100000L;
        ReflectionTestUtils.setField(hashGenerator, "maxRange", maxRange);
    }

    @Test
    public void testGenerateHash() {
        List<Long> range = List.of(10L, 20L, 30L, 40L);
        List<Hash> hashes = prepareHashes();
        when(hashRepository.getUniqueNumbers(maxRange)).thenReturn(range);
        when(base62Encoder.encode(range)).thenReturn(prepareStringHashes());

        hashGenerator.generateHash();

        verify(hashRepository).saveAll(hashes);
        verify(hashGenerator, times(1)).generateHash();
    }

    @Test
    public void testGetHashesIfAmountLessThanSize() {
        long amount = 3L;
        List<String> expectedHashAfterGet = prepareStringHashes();
        List<Hash> hashes = prepareHashes();
        when(hashRepository.getHashBatch(amount)).thenReturn(hashes);

        List<String> actualHashes = hashGenerator.getHashes(amount);

        assertEquals(expectedHashAfterGet, actualHashes);
        verify(hashRepository, times(1)).getHashBatch(anyLong());
    }

    @Test
    public void testGetHashesIfAmountGreaterThanSize() {
        long amount = 10L;
        List<String> expectedHashAfterGet = prepareStringHashes();
        List<Hash> hashes = prepareHashes();
        when(hashRepository.getHashBatch(amount)).thenReturn(hashes);


        List<String> actualHashes = hashGenerator.getHashes(amount);

        assertEquals(expectedHashAfterGet, actualHashes);
        verify(hashGenerator, times(1)).generateHash();
        verify(hashRepository, times(2)).getHashBatch(amount);
    }

    private List<String> prepareStringHashes() {
        return new ArrayList<>(Arrays.asList("ds2", "wewe2", "sad21", "sgfdg2"));
    }

    private List<Hash> prepareHashes() {
        return new ArrayList<>(Arrays.asList(new Hash("ds2"), new Hash("wewe2"), new Hash("sad21"), new Hash("sgfdg2")));
    }
}
