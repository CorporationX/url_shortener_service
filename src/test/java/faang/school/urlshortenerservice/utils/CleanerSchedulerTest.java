package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.service.HashCleanerService;
import faang.school.urlshortenerservice.util.CleanerScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CleanerSchedulerTest {

    @Mock
    private HashCleanerService hashCleanerService;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Test
    void cleanupOutdatedHashes_shouldInvokeHashCleanerService() {
        cleanerScheduler.cleanupOutdatedHashes();

        verify(hashCleanerService).cleanupOutdatedHashes();
    }
}
