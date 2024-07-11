package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepositoryJdbc;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    private static final int HASH_BATCH_SIZE = 5;
    private static final int HASH_SIZE = 6;

    @Mock
    private HashRepositoryJdbc hashRepositoryJdbc;
    @Spy
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    @Test
    public void generateBatchTest() {
        List<String> expectedHashes = List.of("15ftgG", "2BLnMW", "3H1h2m", "4Mhaj2", "5SNUPI");
        ReflectionTestUtils.setField(hashGenerator, "hashBatchSize", HASH_BATCH_SIZE);
        ReflectionTestUtils.setField(base62Encoder, "hashSize", HASH_SIZE);

        when(hashRepositoryJdbc.getUniqueNumbers(HASH_BATCH_SIZE)).thenReturn(List.of(1L, 2L, 3L, 4L, 5L));

        hashGenerator.generateBatch();

        verify(hashRepositoryJdbc).save(expectedHashes);
    }
}