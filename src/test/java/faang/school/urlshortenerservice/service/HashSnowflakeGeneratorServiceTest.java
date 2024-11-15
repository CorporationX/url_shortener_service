package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.encoder.Encoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashSnowflakeGeneratorServiceTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Encoder<Long, Hash> encoder;

    @InjectMocks
    private HashSnowflakeGeneratorService hashSnowflakeGeneratorService;

    @Test
    void testGenerateFreeHashes() {
        int batchSize = 1;
        Hash expectedHash = new Hash("123abc");
        ReflectionTestUtils.setField(hashSnowflakeGeneratorService, "batchSizeForGenerateFreeHashes", batchSize);
        when(encoder.encode(anyLong())).thenReturn(expectedHash);

        hashSnowflakeGeneratorService.generateFreeHashes();

        verify(hashRepository).saveAll(List.of(expectedHash));
        verify(encoder, times(batchSize)).encode(anyLong());
    }

    @Test
    void testGenerateFreeHashes_SavesCorrectNumberOfHashes() {
        int batchSize = 3;
        ReflectionTestUtils.setField(hashSnowflakeGeneratorService, "batchSizeForGenerateFreeHashes", batchSize);

        Hash hash1 = new Hash("123"),
                hash2 = new Hash("456"),
                hash3 = new Hash("789");
        List<Hash> expectedHashes = List.of(hash1, hash2, hash3);

        when(encoder.encode(anyLong())).thenReturn(hash1)
                .thenReturn(hash2)
                .thenReturn(hash3);

        hashSnowflakeGeneratorService.generateFreeHashes();

        verify(hashRepository).saveAll(expectedHashes);
        verify(encoder, times(batchSize)).encode(anyLong());
    }
}