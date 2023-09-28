package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {
    @InjectMocks
    private HashGenerator hashGenerator;
    @Mock
    private HashRepository hashRepository;
    @Mock
    private Base62Encoder encoder;
    private int batchSize;
    private List<Long> emptyIds;

    @BeforeEach
    void setUp() {
        batchSize = 10;
        emptyIds = List.of(1L, 2L, 3L);

        when(hashRepository.findByValueIsNull(PageRequest.of(0, batchSize))).thenReturn(emptyIds.stream()
                .map(id -> Hash.builder().id(id).build())
                .toList());

        when(encoder.encodeSequence(emptyIds)).thenReturn(List.of(
                Hash.builder().id(1L).value("A").build(),
                Hash.builder().id(2L).value("B").build(),
                Hash.builder().id(3L).value("C").build()
        ));
    }

    @Test
    void generateBatchTest() {
        hashGenerator.generateBatch(batchSize);

        verify(hashRepository, times(1)).findByValueIsNull(PageRequest.of(0, batchSize));
        verify(encoder, times(1)).encodeSequence(emptyIds);
        verify(hashRepository, times(1)).saveAll(anyList());
    }
}