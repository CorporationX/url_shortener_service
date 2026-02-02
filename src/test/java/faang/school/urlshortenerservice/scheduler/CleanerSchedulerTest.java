package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashJdbcRepository;
import faang.school.urlshortenerservice.repository.UrlJdbcRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CleanerSchedulerTest {

    @Mock
    private UrlJdbcRepository urlJdbcRepository;

    @Mock
    private HashJdbcRepository hashJdbcRepository;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Test
    void testCleanOldUrls() {
        List<String> freedHashes = List.of("hash1", "hash2", "hash3");
        when(urlJdbcRepository.deleteOldUrlsAndReturnHashes()).thenReturn(freedHashes);

        cleanerScheduler.cleanOldUrls();

        verify(urlJdbcRepository).deleteOldUrlsAndReturnHashes();
        verify(hashJdbcRepository).save(freedHashes);
    }

    @Test
    void testCleanOldUrlsWhenNoOldUrls() {
        when(urlJdbcRepository.deleteOldUrlsAndReturnHashes()).thenReturn(List.of());

        cleanerScheduler.cleanOldUrls();

        verify(urlJdbcRepository).deleteOldUrlsAndReturnHashes();
        verify(hashJdbcRepository, never()).save(anyList());
    }
}

