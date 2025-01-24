package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {
    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    private final long batchSize = 3;

    private List<Long> mockRange = Arrays.asList(1L, 2L, 3L);
    private List<String> encodedHashes = Arrays.asList("abc", "bcd", "cde");
    private List<Hash> expectedHashes = Arrays.asList(new Hash("abc"), new Hash("bcd"), new Hash("cde"));

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashGenerator, "batchSize", 3);
    }

    @Test
    void getHashListSuccessTest() {
        long quantity = 3L;

        when(hashRepository.getHashesAndDelete(quantity)).thenReturn(encodedHashes);

        List<String> actualHashes = hashGenerator.getHashList(quantity);

        assertEquals(expectedHashes.size(), actualHashes.size());
        verify(hashRepository, times(1)).getHashesAndDelete(quantity);
        verify(hashRepository, never()).saveAll(any());
    }

    @Test
    void getHashListWithGenerationHashSuccessTest() {
        long quantity = 3L;
        List<String> hashes = Arrays.asList("aaa");

        when(hashRepository.getHashesAndDelete(quantity)).thenReturn(hashes);
        when(hashRepository.getNextRangeHashes(quantity)).thenReturn(mockRange);
        when(base62Encoder.encode(mockRange)).thenReturn(encodedHashes);
        when(hashRepository.saveAll(any())).thenReturn(List.of(new Hash()));

        List<String> actualHashes = hashGenerator.getHashList(quantity);

        assertEquals(1, actualHashes.size());
        verify(hashRepository, times(1)).getHashesAndDelete(quantity);
        verify(hashRepository, times(1)).getHashesAndDelete(2);
        verify(hashRepository, times(1)).getNextRangeHashes(quantity);
        verify(base62Encoder, times(1)).encode(mockRange);
        verify(hashRepository, times(1)).saveAll(any());
    }
}