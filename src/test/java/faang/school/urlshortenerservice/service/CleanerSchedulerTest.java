package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

class CleanerSchedulerTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void cleanOldUrlsDeleteOldUrlsAndSaveHashesSuccessTest() {
        List<String> expiredHashes = Arrays.asList("hash1", "hash2", "hash3");
        when(urlRepository.deleteOldUrlsAndReturnHashes()).thenReturn(expiredHashes);

        cleanerScheduler.cleanOldUrls();

        verify(urlRepository).deleteOldUrlsAndReturnHashes();
        verify(hashRepository).saveAllHashes(expiredHashes);
    }

    @Test
    void cleanOldUrlErrorOnExceptionFailTest() {
        RuntimeException exception = new RuntimeException("Database error");
        when(urlRepository.deleteOldUrlsAndReturnHashes()).thenThrow(exception);
        try {
            cleanerScheduler.cleanOldUrls();
        } catch (Exception ignored) {
        }
        verify(urlRepository).deleteOldUrlsAndReturnHashes();
        verifyNoInteractions(hashRepository);
    }
}