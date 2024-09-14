package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {

    @InjectMocks
    private HashGenerator hashGenerator;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashGenerator, "uniqueNumbersAmount", 10);
    }

    @Test
    void generateBatchShouldCallHashRepositoryAndBase62Encoder() {
        List<Long> uniqueNumbers = List.of(1L, 2L, 3L);
        List<String> encodedHashes = List.of("hash1", "hash2", "hash3");
        when(hashRepository.getUniqueNumbers(10)).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(uniqueNumbers)).thenReturn(encodedHashes);

        hashGenerator.generateBatch();

        verify(hashRepository).getUniqueNumbers(10);
        verify(base62Encoder).encode(uniqueNumbers);
        verify(hashRepository).save(encodedHashes);
    }

    @Test
    void saveHashesShouldCallHashRepositorySave() {
        List<String> hashes = List.of("hash1", "hash2", "hash3");

        hashGenerator.saveHashes(hashes);

        verify(hashRepository).save(hashes);
    }


    @Test
    void getHashesShouldReturnHashesWhenRepositoryHasSufficientHashes() {
        int count = 5;
        List<Hash> hashesFromRepo = List.of(new Hash("hash1"), new Hash("hash2"), new Hash("hash3"));
        List<String> expectedHashes = List.of("hash1", "hash2", "hash3");
        when(hashRepository.getSize()).thenReturn(10L);
        when(hashRepository.getHashes(count)).thenReturn(hashesFromRepo);

        List<String> result = hashGenerator.getHashes(count);

        assertEquals(expectedHashes, result);
        verify(hashRepository).getSize();
        verify(hashRepository).getHashes(count);
    }
}
