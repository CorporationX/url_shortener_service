package faang.school.urlshortenerservice.service.cleaner;

import faang.school.urlshortenerservice.service.url.UrlService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CleanerSchedulerTest {

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Mock
    private UrlService urlService;

    @Test
    @DisplayName("Success when clean DB")
    public void whenCleanDBThenDeleteOldUrlAndGetHashes() {
        cleanerScheduler.cleanDB();

        verify(urlService).releaseExpiredHashes();
    }
}