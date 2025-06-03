package faang.school.urlshortenerservice.Service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

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
    void positiveCleanOldUrlsShouldDeleteOldUrlsAndSaveHashes() {
        List<String> deletedHashes = List.of("hash1", "hash2");
        when(urlRepository.deleteOlderThan(Mockito.any())).thenReturn(deletedHashes);

        cleanerScheduler.cleanOldUrls();

        verify(urlRepository).deleteOlderThan(Mockito.any());
        verify(hashRepository).saveHashesByBatch(deletedHashes);
    }

    @Test
    void positiveCleanOldUrlsShouldDeleteOldUrlsAndNotSaveHashes() {
        when(urlRepository.deleteOlderThan(Mockito.any())).thenReturn(Collections.emptyList());

        cleanerScheduler.cleanOldUrls();

        verify(urlRepository).deleteOlderThan(Mockito.any());
        verify(hashRepository, Mockito.never()).saveHashesByBatch(Mockito.any());
    }
}
