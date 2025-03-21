package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashJdbcRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorServiceTest {

    @Mock
    private HashJdbcRepository hashJdbcRepository;

    @Mock
    private BaseEncoder baseEncoder;

    @InjectMocks
    private HashGeneratorService hashGeneratorService;

    @Test
    void testGenerateHash() {
        List<Long> uniqueNumbers = Arrays.asList(1L, 2L, 3L);
        List<String> hashes = Arrays.asList("hash1", "hash2", "hash3");
        when(hashJdbcRepository.getUniqueNumbers()).thenReturn(uniqueNumbers);
        when(baseEncoder.encode(uniqueNumbers)).thenReturn(hashes);
        doNothing().when(hashJdbcRepository).save(hashes);

        hashGeneratorService.generateHashBatch();

        verify(hashJdbcRepository).getUniqueNumbers();
        verify(baseEncoder).encode(uniqueNumbers);
        verify(hashJdbcRepository).save(hashes);
    }
}