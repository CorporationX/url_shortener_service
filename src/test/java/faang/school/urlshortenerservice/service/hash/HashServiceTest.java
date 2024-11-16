package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static faang.school.urlshortenerservice.test.utils.TestData.HASHES;
import static faang.school.urlshortenerservice.test.utils.TestData.NUMBERS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashServiceTest {
    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private HashService hashService;

    @SuppressWarnings("unchecked")
    @Test
    void testSaveAllBatch_successful() {
        List<Hash> hashes = HASHES.stream()
                .map(Hash::new)
                .toList();
        hashService.saveAllBatch(hashes);

        ArgumentCaptor<List<Hash>> hashesCaptor = ArgumentCaptor.forClass(List.class);

        verify(hashRepository).saveAll(hashesCaptor.capture());

        assertThat(hashesCaptor.getValue()).isEqualTo(hashes);
    }

    @Test
    void testFindAllByPackSize_successful() {
        when(hashRepository.findAllAndDeletePack(HASHES.size())).thenReturn(HASHES);

        assertThat(hashService.findAllByPackSize(HASHES.size()))
                .isNotNull()
                .isEqualTo(HASHES);

        ArgumentCaptor<Integer> packSizeCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(hashRepository).findAllAndDeletePack(packSizeCaptor.capture());

        assertThat(packSizeCaptor.getValue()).isEqualTo(HASHES.size());
    }

    @Test
    void testGetUniqueNumbers_successful() {
        when(hashRepository.getUniqueNumbers(HASHES.size())).thenReturn(NUMBERS);

        assertThat(hashService.getUniqueNumbers(HASHES.size()))
                .isNotNull()
                .isEqualTo(NUMBERS);

        ArgumentCaptor<Integer> sizeCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(hashRepository).getUniqueNumbers(sizeCaptor.capture());

        assertThat(sizeCaptor.getValue()).isEqualTo(HASHES.size());
    }

    @Test
    void testGetHashesSize() {
        when(hashRepository.getHashesSize()).thenReturn(1L);
        assertThat(hashService.getHashesSize())
                .isEqualTo(1L);
    }
}
