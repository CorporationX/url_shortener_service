package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    private final int maxRange = 100;

    @BeforeEach
    void setUp() {
        hashGenerator = new HashGenerator(hashRepository, base62Encoder);
        hashGenerator.setMaxRange(maxRange);
    }

    @Test
    void testGenerateHashes() {

        List<Long> numbers = List.of(1L, 2L, 3L);
        List<String> hashes = List.of("a", "b", "c");

        when(hashRepository.getNextRange(maxRange)).thenReturn(numbers);
        when(base62Encoder.encode(numbers)).thenReturn(hashes);


        hashGenerator.generateHashes();

        verify(hashRepository, times(1)).getNextRange(maxRange);
        verify(base62Encoder, times(1)).encode(numbers);
        verify(hashRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testSaveHashes() {
        List<String> hashes = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            hashes.add("hash" + i);
        }
        hashGenerator.saveHashes(hashes);
    }

    @Test
    void testSaveHashes_EmptyList() {
        List<String> hashes = List.of();

        hashGenerator.saveHashes(hashes);

        verify(hashRepository, never()).saveAll(anyList());
    }

    @Test
    void testGenerateHashes_EmptyNumbers() {
        List<Long> numbers = List.of();

        when(hashRepository.getNextRange(maxRange)).thenReturn(numbers);

        hashGenerator.generateHashes();

        verify(hashRepository, times(1)).getNextRange(maxRange);
        verify(base62Encoder, never()).encode(anyList());
        verify(hashRepository, never()).saveAll(anyList());
    }
}
