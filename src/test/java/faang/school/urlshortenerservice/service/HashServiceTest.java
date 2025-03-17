package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashServiceTest {
    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private HashService hashService;

    @Test
    void testFindAllByPackSize_withEnoughHashes() {
        List<String> hashes = List.of("abc123", "xyz456");

        when(hashRepository.findAllAndDeleteByPackSize(2)).thenReturn(hashes);

        List<String> result = hashService.findAllByPackSize(2);

        assertThat(result).isEqualTo(hashes);
        verify(hashRepository, times(1)).findAllAndDeleteByPackSize(2);
    }

    @Test
    void testFindAllByPackSize_withInsufficientHashes() {
        List<String> storedHashes = List.of("abc123");

        when(hashRepository.findAllAndDeleteByPackSize(2)).thenReturn(storedHashes);

        assertThatThrownBy(() -> hashService.findAllByPackSize(2))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Not enough hashes available, try again later.");

        verify(hashRepository, times(1)).findAllAndDeleteByPackSize(2);
    }
}