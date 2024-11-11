package faang.school.urlshortenerservice.scheduler.url;

import faang.school.urlshortenerservice.service.url.UrlCleanerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UrlCleanerSchedulerTest {
    @Mock
    private UrlCleanerService urlCleanerService;

    @InjectMocks
    private UrlCleanerScheduler urlCleanerScheduler;

    @Test
    void testExecute_successful() {
        urlCleanerScheduler.execute();
        verify(urlCleanerService).removeExpiredUrls();
    }
}
