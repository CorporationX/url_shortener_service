package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.generator.HashGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashServiceTest {

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private HashCache hashCache;

    @InjectMocks
    private HashService hashService;

    private final List<String> testHashes = List.of("a", "b");
    private final String testHash = "ab";

    @Test
    void getNextHash_shouldReturnFromCache() {
        when(hashCache.poll()).thenReturn(testHash);
        assertEquals(testHash, hashService.getNextHash());
    }

    @Test
    void generateMoreHashes_shouldGenerateAndAddToCache() {
        when(hashGenerator.generateBatch()).thenReturn(testHashes);

        hashService.generateMoreHashes();

        verify(hashCache).addAll(testHashes);
    }
}
