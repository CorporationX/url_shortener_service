package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.postgres.hash.HashRepository;
import faang.school.urlshortenerservice.service.hash.HashService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {
    private static final int BATCH_SIZE = 2;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @Mock
    private HashService hashService;

    @InjectMocks
    private HashGenerator hashGenerator;

    @Test
    void testGenerateBatchOfHashes() {
        List<Long> uniqueNumbers = List.of(1L, 1_000_000_000L);
        when(hashRepository.getUniqueNumbers(eq(BATCH_SIZE))).thenReturn(uniqueNumbers);
        List<String> hashes = List.of("000001", "1fY7ac");
        when(base62Encoder.encode(eq(uniqueNumbers))).thenReturn(hashes);

        hashGenerator.generateBatchOfHashes(BATCH_SIZE);

        verify(hashService).saveAll(eq(hashes));
    }

    @Test
    void testGenerateBatchOfHashesAsync() {
        List<Long> uniqueNumbers = List.of(1L, 1_000_000_000L);
        when(hashRepository.getUniqueNumbers(eq(BATCH_SIZE))).thenReturn(uniqueNumbers);
        List<String> hashes = List.of("000001", "1fY7ac");
        when(base62Encoder.encode(eq(uniqueNumbers))).thenReturn(hashes);

        hashGenerator.generateBatchOfHashesAsync(BATCH_SIZE);

        verify(hashService).saveAll(eq(hashes));
    }

    @Test
    void testGetHashes() {
        List<String> hashes = List.of("000001", "1fY7ac");
        when(hashRepository.getBatchAndDelete(eq(BATCH_SIZE))).thenReturn(hashes);
        List<String> result = hashGenerator.getHashes(BATCH_SIZE);
        assertEquals(hashes, result);
    }

    @Test
    void testGetHashCount() {
        int hashesCount = 666;
        when(hashRepository.getHashesCount()).thenReturn(hashesCount);
        int result = hashGenerator.getHashesCount();
        assertEquals(hashesCount, result);
    }
}