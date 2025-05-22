package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.BaseEncoder;
import faang.school.urlshortenerservice.enity.FreeHash;
import faang.school.urlshortenerservice.properties.HashProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @InjectMocks
    private HashGenerator hashGenerator;

    @Mock
    private HashRepository hashRepository;


    @Mock
    private HashProperties hashProperties;

    @Mock
    private BaseEncoder baseEncoder;

    private final List<Long> sequences = List.of(1L, 2L, 3L, 4L, 5L);


    @Test
    void generate() {
        int generateCount = 5;
        when(hashProperties.getGenerateCount()).thenReturn(generateCount);
        when(hashRepository.getSequences(generateCount)).thenReturn(sequences);
        sequences.forEach(num -> when(baseEncoder.encode(num)).thenReturn(String.valueOf(num)));

        List<FreeHash> hashes = hashGenerator.generate();

        IntStream.range(0, generateCount)
                .forEach(num -> assertEquals(String.valueOf(sequences.get(num)), hashes.get(num).getHash()));
        verify(hashProperties, times(1)).getGenerateCount();
        verify(hashRepository, times(1)).getSequences(generateCount);
        verify(baseEncoder, times(generateCount)).encode(anyLong());
    }
}