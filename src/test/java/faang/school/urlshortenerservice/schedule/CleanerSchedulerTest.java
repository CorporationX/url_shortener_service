package faang.school.urlshortenerservice.schedule;

import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.HashService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CleanerSchedulerTest {

    @Mock
    private UrlCacheRepository urlCacheRepository;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private HashService hashService;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Test
    public void givenStartScheduler_whenCronExpressionIsProvided_thenCorrect() {
        cleanerScheduler.deleteOldUrl();

        verify(urlRepository, times(1)).deleteExpiredUrls();
        verify(urlCacheRepository, times(0)).removeUrl(anyString());
        verify(hashService, times(1)).saveFreeHashes(anyList());
    }
}
