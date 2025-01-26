package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Period;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCleanerSchedulerTest {
    @Mock
    private UrlService urlService;

    @InjectMocks
    private HashCleanerScheduler cleanerScheduler;

    private String cleaningPeriod;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cleanerScheduler, "cleaningPeriod", "P1Y");
    }

    @Test
    void cleanSuccessTest() {
        List<String> expiredHashes = Arrays.asList("hash1", "hash2", "hash3");
        when(urlService.cleanUrls(any(Period.class))).thenReturn((long) expiredHashes.size());

        cleanerScheduler.clean();

        verify(urlService).cleanUrls(any(Period.class));
    }
}