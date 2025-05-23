package faang.school.urlshortenerservice.service.hash.shedule;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CleanerSchedulerTest {
    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Test
    @DisplayName("Должен успешно очищать старые URL и возвращать хэши")
    public void givenOldUrlsExist_whenCleanupCalled_thenDeletesUrlsAndSavesHashes() {
        List<String> mockHashes = List.of("abc123", "def456");
        when(urlRepository.deleteUrlsOlderThanOneYearAndGetHashes()).thenReturn(mockHashes);

        cleanerScheduler.cleanupOldUrlsAndRecycleHashes();

        verify(urlRepository, times(1)).deleteUrlsOlderThanOneYearAndGetHashes();
        verify(hashRepository, times(1)).saveAll(mockHashes);
    }
}
