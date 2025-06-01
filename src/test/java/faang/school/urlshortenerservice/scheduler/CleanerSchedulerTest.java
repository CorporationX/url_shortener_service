package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.properties.UrlProperties;
import faang.school.urlshortenerservice.repository.JdbcHashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.sheduler.CleanerScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CleanerSchedulerTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private JdbcHashRepository jdbcHashRepository;

    @Mock
    private UrlProperties urlProperties;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    private final List<String> hashes = List.of("abc", "xyz");

    @Test
    void cleanOld_shouldSaveReturnedHashesWhenNotEmpty() {
        when(urlProperties.getRetentionPeriod()).thenReturn(Duration.ofDays(365));
        when(urlRepository.deleteOldAndReturnHashes("365 days")).thenReturn(hashes);

        cleanerScheduler.cleanOld();

        verify(urlProperties).getRetentionPeriod();
        verify(urlRepository).deleteOldAndReturnHashes("365 days");
        verify(jdbcHashRepository).save(hashes);
    }

    @Test
    void cleanOld_shouldNotCallSaveWhenNoHashesReturned() {
        when(urlProperties.getRetentionPeriod()).thenReturn(Duration.ofDays(365));
        when(urlRepository.deleteOldAndReturnHashes("365 days")).thenReturn(List.of());

        cleanerScheduler.cleanOld();

        verify(urlProperties).getRetentionPeriod();
        verify(urlRepository).deleteOldAndReturnHashes("365 days");
        verify(jdbcHashRepository, never()).save(anyList());
    }
}
