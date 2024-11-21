package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.generator.Base62Encoder;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static utils.TestData.generateHashes;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {
    @Mock
    private HashRepository hashRepository;
    @Spy
    private Base62Encoder encoder;
    @InjectMocks
    private HashGenerator hashGenerator;

    private int batchSize = 20;

    @BeforeEach
    void setUp() throws Exception {
        Field batchSize = HashGenerator.class.getDeclaredField("batchSize");
        batchSize.setAccessible(true);
        batchSize.set(hashGenerator, this.batchSize);
    }

    @Test
    public void testGetHashes() {
        List<String> hashes = generateHashes(10, 6);

        when(hashRepository.getHashBatch(hashes.size())).thenReturn(hashes);
        when(hashRepository.getHashesSize()).thenReturn((long) hashes.size());
        List<String> result = hashGenerator.getHashes(hashes.size());

        assertEquals(result, hashes);
    }

    @Test
    public void testGetHashesLowHashes() {
        int amount = 20;
        List<String> hashes = generateHashes(10, 6);

        List<Long> range = LongStream.range(0, amount).boxed().toList();
        when(hashRepository.getHashesSize()).thenReturn((long) hashes.size());
        when(hashRepository.getUniqueNumbers(amount)).thenReturn(range);

        List<String> result = hashGenerator.getHashes(amount);

        assertEquals(result.size(), 20);
    }
}
