package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {
    @Mock
    private HashRepository hashRepository;
    @Mock
    private Base62Encoder base62Encoder;
    @InjectMocks
    private HashGenerator hashGenerator;

    @BeforeEach
    public void setUp() {
        setField(hashGenerator, "countGenerateHashes", 10);
        setField(hashGenerator, "minHashRatio", 0.5);
    }

    @Test
    void givenFewHashes_whenCheckAndGenerateHashes_thenGenerateHashes() {
        when(hashRepository.getCountOfHashes()).thenReturn(3L);
        when(hashRepository.getUniqueNumbers(10)).thenReturn(List.of(1L, 2L, 3L));
        when(base62Encoder.encode(anyList())).thenReturn(List.of("a", "b", "c"));

        hashGenerator.checkAndGenerateHashesAsync();

        verify(hashRepository).getUniqueNumbers(10);
        verify(base62Encoder).encode(anyList());
        verify(hashRepository).saveHashes(anyList());
    }

    @Test
    void givenEnoughHashes_whenCheckAndGenerateHashes_thenDoNothing() {
        when(hashRepository.getCountOfHashes()).thenReturn(10L);

        hashGenerator.checkAndGenerateHashesAsync();

        verify(hashRepository, never()).getUniqueNumbers(anyInt());
        verify(base62Encoder, never()).encode(anyList());
        verify(hashRepository, never()).saveHashes(anyList());
    }

}
