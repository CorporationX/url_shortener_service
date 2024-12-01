package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
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

    @Test
    void generateBatch_shouldGenerateAndSaveHashes() {
        List<Long> uniqueNumbers = List.of(1L, 2L, 3L);
        List<String> hashes = List.of("hash1", "hash2", "hash3");

        when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(uniqueNumbers)).thenReturn(hashes);

        hashGenerator.generateBatch();

        verify(hashRepository).saveAll(argThat((List<Hash> hashesList) -> hashesList.size() == hashes.size()));
    }
}