package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CleanerSchedulerTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Test
    void testUrlCleaner_ShouldDoNothing_WhenNoOldLinksFound() {
        when(urlRepository.removeOldLinksAndReturnHash()).thenReturn(List.of());

        cleanerScheduler.urlCleaner();

        verify(urlRepository).removeOldLinksAndReturnHash();
        verify(hashRepository, never()).save(anyList());
    }

    @Test
    void testUrlCleaner_ShouldSaveHashes_WhenOldLinksExist() {
        List<String> oldHashes = List.of("hash1", "hash2", "hash3");
        when(urlRepository.removeOldLinksAndReturnHash()).thenReturn(oldHashes);

        cleanerScheduler.urlCleaner();

        verify(urlRepository).removeOldLinksAndReturnHash();
        verify(hashRepository).save(oldHashes);
    }

}