package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.service.hash.util.HashGenerator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    void testSaveAllBatch_successful() {
        List<String> hashes = List.of("abc123", "xyz789");

        hashService.saveAllBatch(hashes);

        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        verify(hashRepository).save(captor.capture());

        assertThat(captor.getValue()).isEqualTo(hashes);
    }

    @Test
    void testFindAllByPackSize_successful() {
        when(hashRepository.getHashBatch(2)).thenReturn(List.of("abc123", "xyz789"));

        List<String> result = hashService.findAllByPackSize(2);

        assertThat(result).containsExactly("abc123", "xyz789");
        verify(hashRepository).getHashBatch(2);
    }

    @Test
    void testFindAllByPackSize_notEnoughHashes() {
        when(hashRepository.getHashBatch(2)).thenReturn(List.of("abc123"));
        when(hashGenerator.generateAndGet(1)).thenReturn(List.of("xyz789"));

        List<String> result = hashService.findAllByPackSize(2);

        assertThat(result).containsExactly("abc123", "xyz789");
        verify(hashRepository).getHashBatch(2);
        verify(hashGenerator).generateAndGet(1);
    }

    @Test
    void testGetUniqueNumbers_successful() {
        when(hashRepository.getUniqueNumbers(2)).thenReturn(List.of(100L, 101L));

        List<Long> result = hashService.getUniqueNumbers(2L);

        assertThat(result).containsExactly(100L, 101L);
        verify(hashRepository).getUniqueNumbers(2);
    }

    @Test
    void testGetHashesSize() {
        when(hashRepository.count()).thenReturn(5L);

        long size = hashService.getHashesSize();
        assertThat(size).isEqualTo(5L);
        verify(hashRepository).count();
    }
}