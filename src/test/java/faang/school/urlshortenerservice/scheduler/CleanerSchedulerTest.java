package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.JdbcHashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.sheduler.CleanerScheduler;
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
    private UrlRepository urlRepository;

    @Mock
    private JdbcHashRepository jdbcHashRepository;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    private final List<String> hashes = List.of("abc", "xyz");

    @Test
    void cleanOld_shouldSaveReturnedHashesWhenNotEmpty() {
        when(urlRepository.deleteOldAndReturnHashes()).thenReturn(hashes);

        cleanerScheduler.cleanOld();

        verify(urlRepository).deleteOldAndReturnHashes();
        verify(jdbcHashRepository).save(hashes);
    }

    @Test
    void cleanOld_shouldNotCallSaveWhenNoHashesReturned() {
        when(urlRepository.deleteOldAndReturnHashes()).thenReturn(List.of());

        cleanerScheduler.cleanOld();

        verify(urlRepository).deleteOldAndReturnHashes();
        verify(jdbcHashRepository, never()).save(anyList());
    }
}
