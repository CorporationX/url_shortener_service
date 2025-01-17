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

    @BeforeEach
    void setUp() {
        uniqueSeqNumbers = List.of(1L, 2L, 3L);
        hashes = List.of(Hash.builder().hash("1").build(), Hash.builder().hash("2").build(),
                Hash.builder().hash("3").build());

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
        when(hashRepository.getAndDeleteHashBatch(any())).thenReturn(hashes);

        List<Hash> result = hashService.getAndDeleteHashBatch(1L);

        verify(hashRepository, times(1)).getAndDeleteHashBatch(any());
        assertThat(result).isEqualTo(hashes);
    }

    @Test
    void testSaveHashes() {
        when(hashRepository.saveAll(any())).thenReturn(hashes);

        List<Hash> result = hashService.saveHashes(hashes);

        verify(hashRepository, times(1)).saveAll(any());
        assertThat(result).isEqualTo(hashes);
    }

}