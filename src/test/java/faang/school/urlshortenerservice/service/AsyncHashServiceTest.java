package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.HashGenerationException;
import faang.school.urlshortenerservice.hash.HashGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsyncHashServiceTest {

    @Mock
    private HashGenerator hashGenerator;

    @InjectMocks
    private AsyncHashService asyncHashService;

    private final List<String> testHashes = List.of("hash1", "hash2", "hash3");

    @Test
    void testFillHashCacheAsyncSuccessfully() {
        when(hashGenerator.getHashes()).thenReturn(testHashes);

        asyncHashService.fillHashCacheAsync();

        verify(hashGenerator).getHashes();
    }

    @Test
    void testFillHashCacheAsyncWhenExceptionHandled() {
        when(hashGenerator.getHashes()).thenThrow(new HashGenerationException("Test exception"));

        asyncHashService.fillHashCacheAsync();

        verify(hashGenerator).getHashes();
    }
}
