package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {

    @InjectMocks
    private HashGenerator hashGenerator;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Encoder encoder;

    @Test
    public void testGenerateBatch() {
        // arrange
        List<Integer> numbers = List.of(1, 2, 555, 123123, 88868, 1000985);
        List<String> hashes = List.of("000001", "000002", "00008x", "000W1r", "000N7M", "004COv");
        when(hashRepository.getNUniqueNumbers(0)).thenReturn(numbers);
        when(encoder.encodeNumbers(numbers)).thenReturn(hashes);

        // act
        hashGenerator.generateBatch();

        // assert
        verify(hashRepository).save(hashes);
    }
}
