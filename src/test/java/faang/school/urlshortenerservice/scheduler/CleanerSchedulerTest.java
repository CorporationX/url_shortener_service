package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
public class CleanerSchedulerTest {

    @Mock
    private UrlService urlService;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Test
    public void testClearUnusedHashes() {
        cleanerScheduler.clearUnusedHashes();

        verify(urlService).clearOutdatedUrls();
    }
}