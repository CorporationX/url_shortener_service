package faang.school.urlshortenerservice.component;

import faang.school.urlshortenerservice.repository.interfaces.HashRepository;
import faang.school.urlshortenerservice.repository.interfaces.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
    void testCleanOldUrlsDeletesAndSavesHashes() {
        List<String> expiredHashes = Arrays.asList("hash1", "hash2");
        when(urlRepository.deleteOlderThan(any(LocalDateTime.class))).thenReturn(expiredHashes);
        cleanerScheduler.cleanOldUrls();
        verify(urlRepository).deleteOlderThan(any(LocalDateTime.class));
        verify(hashRepository).save(expiredHashes);
    }

    @Test
    void testCleanOldUrlsNoExpiredUrls() {
        when(urlRepository.deleteOlderThan(any(LocalDateTime.class))).thenReturn(List.of());
        cleanerScheduler.cleanOldUrls();
        verify(urlRepository).deleteOlderThan(any(LocalDateTime.class));
        verify(hashRepository, never()).save(anyList());
    }
}