package faang.school.urlshortenerservice.schedule;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CleanerSchedulerTest {
    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @Test
    void testCleanOldUrls() {
        List<String> hashes = List.of("hash1", "hash2", "hash3");

        when(urlRepository.deleteUrlsOlderThan(any(LocalDateTime.class))).thenReturn(hashes);

        cleanerScheduler.cleanOldUrls();
        hashes.forEach(hash -> verify(hashRepository).save(new Hash(hash)));
    }

    @Test
    void testCleanOldUrlsEmpty() {
        List<String> hashes = List.of();
        when(urlRepository.deleteUrlsOlderThan(any(LocalDateTime.class))).thenReturn(hashes);

        cleanerScheduler.cleanOldUrls();

        verify(urlRepository).deleteUrlsOlderThan(any(LocalDateTime.class));
        verifyNoMoreInteractions(urlRepository);
    }
}
