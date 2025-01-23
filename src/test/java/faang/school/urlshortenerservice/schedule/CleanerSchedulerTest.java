package faang.school.urlshortenerservice.schedule;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
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

    @BeforeEach
    void setUp() throws Exception {
        Field batchSizeField = CleanerScheduler.class.getDeclaredField("batchSize");
        batchSizeField.setAccessible(true);
        batchSizeField.set(cleanerScheduler, 2);
    }

    @Test
    void testCleanOldUrlsWithHashes() {
        List<String> hashes = List.of("hash1", "hash2", "hash3");
        when(urlRepository.deleteUrlsOlderThan(any(LocalDateTime.class))).thenReturn(hashes);

        cleanerScheduler.cleanOldUrls();

        verify(urlRepository).deleteUrlsOlderThan(any(LocalDateTime.class));
        verify(hashRepository).saveAll(List.of(new Hash("hash1"), new Hash("hash2"), new Hash("hash3")));
        verifyNoMoreInteractions(urlRepository, hashRepository);
    }

    @Test
    void testCleanOldUrlsWithEmptyHashes() {
        when(urlRepository.deleteUrlsOlderThan(any(LocalDateTime.class))).thenReturn(List.of());

        cleanerScheduler.cleanOldUrls();

        verify(urlRepository).deleteUrlsOlderThan(any(LocalDateTime.class));
        verifyNoMoreInteractions(urlRepository);
    }

    @Test
    void testCleanOldUrlsWithBatchSize() {
        List<String> hashes = List.of("hash1", "hash2", "hash3", "hash4", "hash5");
        when(urlRepository.deleteUrlsOlderThan(any(LocalDateTime.class))).thenReturn(hashes);

        cleanerScheduler.cleanOldUrls();

        verify(urlRepository).deleteUrlsOlderThan(any(LocalDateTime.class));
        verify(hashRepository, times(1)).saveAll(anyList());
        verifyNoMoreInteractions(urlRepository, hashRepository);
    }

    @Test
    void testCleanOldUrlsWithException() {
        List<String> hashes = List.of("hash1", "hash2", "hash3");
        when(urlRepository.deleteUrlsOlderThan(any(LocalDateTime.class))).thenReturn(hashes);
        doThrow(new RuntimeException()).when(hashRepository).saveAll(anyList());

        try {
            cleanerScheduler.cleanOldUrls();
        } catch (RuntimeException e) {
            verify(urlRepository).deleteUrlsOlderThan(any(LocalDateTime.class));
            verify(hashRepository).saveAll(anyList());
            verifyNoMoreInteractions(urlRepository, hashRepository);
        }
    }

}
