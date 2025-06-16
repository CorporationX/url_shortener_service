package faang.school.urlshortenerservice.service.hash;


import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test cases of HashServiceImplTest")
public class HashServiceImplTest {

    private static final int DB_THRESHOLD = 100;

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private HashServiceImpl hashService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(hashService, "threshold", DB_THRESHOLD);
    }

    @Test
    @DisplayName("getHashBatch - success")
    public void testGetHashBatchSuccess() {
        int quantity = 10;

        hashService.getHashBatch(quantity);

        verify(hashRepository, times(1)).getHashBatch(quantity);
    }

    @Test
    @DisplayName("save - success")
    public void testSaveSuccess() {
        List<String> hashes = List.of("hash1", "hash2");

        hashService.save(hashes);

        verify(hashRepository, times(1)).save(hashes);
    }

    @Test
    @DisplayName("isNeedGenerateHash - false")
    public void testIsNeedGenerateHashFalse() {
        long aboveThresholdCount = 150;
        when(hashRepository.getHashCount()).thenReturn(aboveThresholdCount);

        boolean result = hashService.isNeedGenerateHash();

        assertFalse(result);
    }

    @Test
    @DisplayName("isNeedGenerateHash - true")
    public void testIsNeedGenerateHashTrue() {
        long belowThresholdCount = 50;
        when(hashRepository.getHashCount()).thenReturn(belowThresholdCount);

        boolean result = hashService.isNeedGenerateHash();

        assertTrue(result);
    }
}
