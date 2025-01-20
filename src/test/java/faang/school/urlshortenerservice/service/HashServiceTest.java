package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashServiceTest {

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private HashService hashService;

    private List<Long> uniqueSeqNumbers;
    private List<Hash> hashes;
    private List<String> stringHashes;

    @BeforeEach
    void setUp() {
        uniqueSeqNumbers = List.of(1L, 2L, 3L);
        hashes = List.of(Hash.builder().hash("1").build(), Hash.builder().hash("2").build(),
                Hash.builder().hash("3").build());
        stringHashes = List.of("1", "2", "3");
    }

    @Test
    void testGetUniqueSeqNumbers() {
        when(hashRepository.getUniqueSeqNumbers(any())).thenReturn(uniqueSeqNumbers);

        List<Long> result = hashService.getUniqueSeqNumbers(1L);

        verify(hashRepository, times(1)).getUniqueSeqNumbers(any());
        assertThat(result).isEqualTo(uniqueSeqNumbers);
    }

    @Test
    void testGetAndDeleteHashBatch() {
        when(hashRepository.getAndDeleteHashBatch(any())).thenReturn(stringHashes);

        List<String> result = hashService.getAndDeleteHashBatch(1L);

        verify(hashRepository, times(1)).getAndDeleteHashBatch(any());
        assertThat(result).isEqualTo(stringHashes);
    }

    @Test
    void testSaveHashes() {
        when(hashRepository.saveAll(any())).thenReturn(hashes);

        hashService.saveHashes(hashes);

        verify(hashRepository, times(1)).saveAll(any());
    }

    @Test
    void testGetHashRepositorySize() {
        when(hashRepository.count()).thenReturn(1L);

        Long result =  hashService.getHashRepositorySize();

        verify(hashRepository, times(1)).count();
        assertThat(result).isEqualTo(1L);
    }
}