package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CleanerSchedulerTest {

    @Mock
    private UrlService urlService;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cleanerScheduler, "retentionPeriod", Duration.ofDays(7));
    }

    @Test
    @DisplayName("Очистка старых URL вызывает сервис с правильной датой")
    void cleanOldUrlsCallsServiceWithCorrectDate() {
        cleanerScheduler.cleanOldUrls();

        LocalDateTime expectedDate = LocalDateTime.now().minus(Duration.ofDays(7));
        verify(urlService).removeOldUrls(argThat(date ->
                ChronoUnit.SECONDS.between(expectedDate, date) <= 1
        ));
    }

    @Test
    @DisplayName("Планировщик вызывает очистку")
    void schedulerCallsCleanMethod() {
        CleanerScheduler spyScheduler = spy(cleanerScheduler);

        spyScheduler.cleanOldUrlsScheduled();

        verify(spyScheduler).cleanOldUrls();
    }
}