package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.CleanerScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CleanerSchedulerTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Test
    void cleanupOutdatedHashes_shouldDeleteOldUrlsAndMoveHashesToHashTable() {
        int days = 365;
        cleanerScheduler.setNumberOfDaysForOutdatedHashes(days);

        List<String> retrievedHashes = List.of("abc123", "def456", "ghi789");
        when(urlRepository.deleteOldUrlsAndReturnHashes(days)).thenReturn(retrievedHashes);

        cleanerScheduler.cleanupOutdatedHashes();

        verify(urlRepository).deleteOldUrlsAndReturnHashes(days);
        verify(hashRepository).save(retrievedHashes);

        verify(urlCacheRepository).deleteByHash("abc123");
        verify(urlCacheRepository).deleteByHash("def456");
        verify(urlCacheRepository).deleteByHash("ghi789");
    }

    @Test
    void cleanupOutdatedHashes_shouldNotSaveHashesWhenNoUrlsFound() {
        int days = 365;
        cleanerScheduler.setNumberOfDaysForOutdatedHashes(days);

        List<String> emptyList = List.of();
        when(urlRepository.deleteOldUrlsAndReturnHashes(days)).thenReturn(emptyList);

        cleanerScheduler.cleanupOutdatedHashes();

        verify(urlRepository).deleteOldUrlsAndReturnHashes(days);
        verify(hashRepository, never()).save(any());
        verifyNoInteractions(urlCacheRepository);
    }
}
