package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.exceptions.CleanUrlException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SchedulerTest {
    @InjectMocks
    private CleanerScheduler cleanerScheduler;
    @Mock
    private HashRepository hashRepository;
    @Mock
    private UrlRepository urlRepository;

    @Test
    public void testPositiveCleanOldUrls() {
        List<String> hashes = List.of("test1", "test2", "test3");
        when(urlRepository.deleteOldUrlsAndReturnHashes()).thenReturn(hashes);
        cleanerScheduler.cleanOldUrls();
        verify(hashRepository, times(1)).save(hashes);
    }

    @Test
    public void testNegativeCleanOldUrls() {
        doThrow(new RuntimeException("DB error"))
                .when(urlRepository).deleteOldUrlsAndReturnHashes();
        assertThrows(CleanUrlException.class, () -> cleanerScheduler.cleanOldUrls());
    }
}
