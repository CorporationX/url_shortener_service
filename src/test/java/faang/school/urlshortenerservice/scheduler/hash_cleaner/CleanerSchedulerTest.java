package faang.school.urlshortenerservice.scheduler.hash_cleaner;

import faang.school.urlshortenerservice.repository.hash.impl.HashRepositoryImpl;
import faang.school.urlshortenerservice.repository.url.impl.UrlRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CleanerSchedulerTest {

    @Mock
    private HashRepositoryImpl hashRepository;

    @Mock
    private UrlRepositoryImpl urlRepository;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Test
    public void cleanUnusedHashesTest() {
        List<String> hashes = new ArrayList<>(List.of("hash1", "hash2", "hash3"));
        when(urlRepository.deleteUrlsOlderThanOneYear()).thenReturn(hashes);

        cleanerScheduler.cleanUnusedHashes();

        verify(urlRepository, times(1)).deleteUrlsOlderThanOneYear();
        verify(hashRepository,times(1)).saveHashes(hashes);
    }

    @Test
    void cleanUnusedHashesWithNoHashesTest() {
        List<String> mockHashes = Collections.emptyList();
        when(urlRepository.deleteUrlsOlderThanOneYear()).thenReturn(mockHashes);

        cleanerScheduler.cleanUnusedHashes();

        verify(urlRepository, times(1)).deleteUrlsOlderThanOneYear();
        verify(hashRepository, times(1)).saveHashes(mockHashes);
    }
}