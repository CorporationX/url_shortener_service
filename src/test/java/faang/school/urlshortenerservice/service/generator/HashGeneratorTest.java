package faang.school.urlshortenerservice.service.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.service.HashService;
import faang.school.urlshortenerservice.service.encoder.Base62Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {
    @InjectMocks
    HashGenerator hashGenerator;
    @Mock
    HashService hashService;
    @Mock
    Base62Encoder base62Encoder;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashGenerator, "countOfGeneratedHashes", 10000);
    }

    @Test
    void testGenerateBatch_Success() {
        int countOfGeneratedHashes = getCountOfGeneratedHashes();
        List<Long> uniqueNumbers = List.of(1L, 2L, 3L);
        List<Hash> encodedHashes = List.of(new Hash("hash1"), new Hash("hash2"), new Hash("hash3"));
        List<Hash> savedHashes = List.of(new Hash("hash1"), new Hash("hash2"), new Hash("hash3"));

        when(hashService.getUniqueNumbers(countOfGeneratedHashes)).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(uniqueNumbers)).thenReturn(encodedHashes);
        when(hashService.saveAll(encodedHashes)).thenReturn(savedHashes);

        CompletableFuture<List<Hash>> resultFuture = hashGenerator.generateBatch();
        List<Hash> result = resultFuture.join();

        assertNotNull(result);
        assertEquals(savedHashes, result);
        verify(hashService, times(1)).getUniqueNumbers(countOfGeneratedHashes);
        verify(base62Encoder, times(1)).encode(uniqueNumbers);
        verify(hashService, times(1)).saveAll(encodedHashes);
    }

    public int getCountOfGeneratedHashes(){
        return (int) ReflectionTestUtils.getField(hashGenerator,"countOfGeneratedHashes");
    }
}
