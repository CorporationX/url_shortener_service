package faang.school.urlshortenerservice.service.generator;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
    void generateHashes_ShouldGenerateAndSaveHashes() {
        ReflectionTestUtils.setField(hashGenerator, "maxRange", 100);
        List<Long> numbers = List.of(1L, 2L, 3L);
        List<Hash> hashes = List.of(
                new Hash("hash1"),
                new Hash("hash2"),
                new Hash("hash3")
        );

        when(hashRepository.getUniqueNumbers(100)).thenReturn(numbers);
        when(base62Encoder.encode(numbers)).thenReturn(hashes);

        hashGenerator.generateHashes();

        verify(hashRepository).getUniqueNumbers(100);
        verify(base62Encoder).encode(numbers);
        verify(hashRepository).saveAll(hashes);
    }

    @Test
    void getHashes_WhenEnoughHashesExist_ShouldReturnHashes() {
        int requestedCount = 3;
        List<Hash> existingHashes = List.of(
                new Hash("h1"),
                new Hash("h2"),
                new Hash("h3")
        );

        when(hashRepository.findAndDelete(requestedCount)).thenReturn(existingHashes);

        List<Hash> result = hashGenerator.getHashes(requestedCount);

        assertThat(result).hasSize(3).containsExactlyElementsOf(existingHashes);
        verify(hashRepository, never()).getUniqueNumbers(anyInt());
        verify(hashRepository, times(1)).findAndDelete(requestedCount);
    }

    @Test
    void getHashes_WhenNotEnoughHashes_ShouldGenerateNew() {
        ReflectionTestUtils.setField(hashGenerator, "maxRange", 100);
        int requestedCount = 5;

        List<Hash> initialHashes = new ArrayList<>(List.of(new Hash("h1"), new Hash("h2")));
        when(hashRepository.findAndDelete(requestedCount)).thenReturn(initialHashes);

        List<Long> newNumbers = new ArrayList<>(List.of(1L, 2L, 3L));
        List<Hash> newHashes = new ArrayList<>(List.of(new Hash("h3"), new Hash("h4"), new Hash("h5")));
        when(hashRepository.getUniqueNumbers(100)).thenReturn(newNumbers);
        when(base62Encoder.encode(newNumbers)).thenReturn(newHashes);

        when(hashRepository.findAndDelete(3)).thenReturn(newHashes.subList(0, 3));

        List<Hash> result = hashGenerator.getHashes(requestedCount);

        assertThat(result).hasSize(5)
                .containsExactlyInAnyOrder(
                        new Hash("h1"), new Hash("h2"),
                        new Hash("h3"), new Hash("h4"), new Hash("h5")
                );

        verify(hashRepository, times(1)).getUniqueNumbers(100);
        verify(hashRepository, times(2)).findAndDelete(anyInt());
    }

    @Test
    void getHashes_WhenZeroOrNegativeCount_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> hashGenerator.getHashes(-1));
        assertThrows(IllegalArgumentException.class, () -> hashGenerator.getHashes(0));
    }
}