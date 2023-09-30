package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class HashGeneratorTest {
    @InjectMocks
    private HashGenerator hashGenerator;
    @Mock
    private HashRepository hashRepository;
    @Mock
    private Base62Encoder encoder;
    private List<Long> emptyIds;

    @BeforeEach
    void setUp() {
        emptyIds = List.of(10L, 20L, 30L);

        when(hashRepository.getUniqueNumbers()).thenReturn(List.of(10L, 20L, 30L));
        when(encoder.encodeSequence(emptyIds)).thenReturn(List.of("A", "B", "C"));
    }

    @Test
    void generateBatchTest() {
        hashGenerator.generateBatch();

        verify(hashRepository, times(1)).getUniqueNumbers();
        verify(encoder, times(1)).encodeSequence(emptyIds);
        verify(hashRepository, times(1)).save(anyList());
    }
}