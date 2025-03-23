package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashGenerator;
import faang.school.urlshortenerservice.util.Base62Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @Mock
    private Base62Encoder encoder;

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private HashGenerator hashGenerator;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashGenerator, "hashLimit", 3);
    }

    @Test
    void testGenerateBatchesSuccess() {
        List<Long> uniqueNumbers = Arrays.asList(1000L, 2000L, 3000L);
        String[] expectedHashes = {"hash1", "hash2", "hash3"};

        when(hashRepository.getUniqueNumbers(3)).thenReturn(uniqueNumbers);
        when(encoder.generateHashes(uniqueNumbers)).thenReturn(expectedHashes);

        CompletableFuture<Void> future = hashGenerator.generateBatches();
        future.join();

        verify(hashRepository).getUniqueNumbers(3);
        verify(encoder).generateHashes(uniqueNumbers);
        verify(hashRepository).saveHashes(expectedHashes);
    }

    @Test
    void testGenerateBatchesFailure() {
        RuntimeException exception = new RuntimeException("Test exception");
        when(hashRepository.getUniqueNumbers(3)).thenThrow(exception);

        CompletableFuture<Void> future = hashGenerator.generateBatches();

        assertTrue(future.isCompletedExceptionally());

        Exception thrown = assertThrows(Exception.class, future::join);
        assertNotNull(thrown.getCause());
        assertEquals("Test exception", thrown.getCause().getMessage());
    }
}
