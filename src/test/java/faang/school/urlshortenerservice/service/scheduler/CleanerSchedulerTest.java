package faang.school.urlshortenerservice.service.scheduler;

import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CleanerSchedulerTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Test
    void cleanOldUrls_ShouldDoNothing_WhenNoOldUrlsFound() {
        when(urlRepository.deleteOldUrls()).thenReturn(List.of());

        cleanerScheduler.cleanOldUrls();

        verify(urlRepository).deleteOldUrls();
        verify(hashRepository, never()).saveAll(any());
    }

    @Test
    void cleanOldUrls_ShouldSaveFreedHashes_WhenOldUrlsExist() {
        List<String> freedHashes = List.of("abc123", "def456");
        when(urlRepository.deleteOldUrls()).thenReturn(freedHashes);

        cleanerScheduler.cleanOldUrls();

        verify(urlRepository).deleteOldUrls();
    }

    @Test
    void cleanOldUrls_ShouldHandleEmptyHashList_WhenRepositoryReturnsEmpty() {
        when(urlRepository.deleteOldUrls()).thenReturn(List.of());

        cleanerScheduler.cleanOldUrls();

        verify(urlRepository).deleteOldUrls();
        verify(hashRepository, never()).saveAll(any());
    }
}