package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.cleaner.CleanerService;
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
    private CleanerService cleanerService;

    @Test
    @DisplayName("When called then should call method from cleaner service")
    void whenCalledThenOneMethodFromCleanerServiceCalls() {
        cleanerScheduler.clearExpiredUrls();

        verify(cleanerService)
                .clearExpiredUrls();
    }
}