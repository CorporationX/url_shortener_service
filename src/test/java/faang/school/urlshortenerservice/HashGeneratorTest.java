package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.entity.Hash;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {
    private static final List<String> HASHES = List.of("8G", "9G", "AG", "BG", "CG", "DG", "EG", "FG", "GG", "HG");

    @Mock
    private HashRepository hashRepository;
    @Spy
    private Base62Encoder encoder;
    @InjectMocks
    private HashGenerator hashGenerator;

    private List<Hash> hashes;
    private int batchSize = 20;

    @BeforeEach
    void setUp() throws Exception {
        Field batchSize = HashGenerator.class.getDeclaredField("batchSize");
        batchSize.setAccessible(true);
        batchSize.set(hashGenerator, this.batchSize);

        hashes = new ArrayList<>(HASHES.stream()
                .map(Hash::new)
                .toList());
    }

    @Test
    public void testGetHashes() {
        when(hashRepository.getHashBatch(HASHES.size())).thenReturn(hashes);
        List<String> result = hashGenerator.getHashes(HASHES.size());

        assertEquals(result, HASHES);
    }

    @Test
    public void testGetHashesLowHashes() {
        int amount = 20;

        List<Long> range = LongStream.range(1, amount).boxed().toList();
        when(hashRepository.getHashBatch(amount)).thenReturn(hashes);
        when(hashRepository.getUniqueNumbers(amount)).thenReturn(range);

        List<String> result = hashGenerator.getHashes(amount);

        assertEquals(result.size(), 10);
    }
}
