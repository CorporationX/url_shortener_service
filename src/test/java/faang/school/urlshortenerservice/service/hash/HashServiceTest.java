package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.service.hash.util.HashGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import static faang.school.urlshortenerservice.test.utils.TestData.HASHES;
import static faang.school.urlshortenerservice.test.utils.TestData.NUMBERS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashServiceTest {
    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashGenerator hashGenerator;

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
    void testFindAllByPackSize_notEnoughHashes() {
        Executor executor = mock(Executor.class);
        ReflectionTestUtils.setField(hashService, "executor", executor);
        List<String> hashes1 = new ArrayList<>(HASHES.subList(0, HASHES.size() - 8));
        List<String> hashes2 = new ArrayList<>(HASHES.subList(hashes1.size(), HASHES.size()));

        when(hashRepository.findAllAndDeletePack(HASHES.size())).thenReturn(hashes1);
        when(hashGenerator.generateAndGet(HASHES.size() - hashes1.size())).thenReturn(hashes2);

        assertThat(hashService.findAllByPackSize(HASHES.size()))
                .isNotNull()
                .isEqualTo(HASHES);

        ArgumentCaptor<Integer> packSizeCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(hashRepository).findAllAndDeletePack(packSizeCaptor.capture());
        assertThat(packSizeCaptor.getValue()).isEqualTo(HASHES.size());

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(executor).execute(runnableCaptor.capture());
        runnableCaptor.getValue().run();
        verify(hashGenerator).generate();
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
