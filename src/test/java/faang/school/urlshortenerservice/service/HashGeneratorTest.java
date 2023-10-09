package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @Mock
    Base62Encoder base62Encoder;
    @Mock
    HashRepository hashRepository;

    @InjectMocks
    HashGenerator hashGenerator;

    @Test
    void testGenerateBatch() {
        List<Long> uniqueNumbers = List.of(1L, 2L, 3L, 125L, 1354123L, 937821L, 31321321312321L);
        List<String> expected = List.of("1", "2", "3", "21", "5gGh", "3vy9", "8tQciGpd");
        when(hashRepository.getUniqueNumbers(0))
                .thenReturn(uniqueNumbers);
        when(base62Encoder.encode(uniqueNumbers))
                .thenReturn(expected);

        CompletableFuture<List<String>> hashes = hashGenerator.generateBatch();

        verify(hashRepository, times(1)).saveAll(anyList());
        try {
            assertEquals(expected, hashes.get());
        } catch (ExecutionException | InterruptedException e) {
            fail();
        }
    }
}