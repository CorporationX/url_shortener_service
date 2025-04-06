package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.sheduler.CleanerScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CleanerSchedulerTest {

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @Test
    void cleanOldUrls_WhenOldUrlsExist_ShouldReclaimHashes() {
        when(urlRepository.deleteOldUrlsAndReturnHashes()).thenReturn(Arrays.asList("hash1", "hash2"));

        cleanerScheduler.cleanOldUrls();

        verify(hashRepository, times(1)).save(Arrays.asList("hash1", "hash2"));
        verify(urlRepository, times(1)).deleteOldUrlsAndReturnHashes();
    }

    @Test
    void cleanOldUrls_WhenNoOldUrls_ShouldNotReclaimHashes() {
        when(urlRepository.deleteOldUrlsAndReturnHashes()).thenReturn(Collections.emptyList());

        cleanerScheduler.cleanOldUrls();

        verify(hashRepository, never()).save(anyList());
        verify(urlRepository, times(1)).deleteOldUrlsAndReturnHashes();
    }
}