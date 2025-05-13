package faang.school.urlshortenerservice.service.scheduler;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.service.CleanerService;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CleanerSchedulerTest {

    @Mock
    private ExecutorService urlCleanupExecutor;

    @Mock
    private CleanerService cleanerService;

    @Mock
    private UrlService urlService;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cleanerScheduler, "batchSize", 100);
        ReflectionTestUtils.setField(cleanerScheduler, "urlExpirationMonths", 1);
    }

    @Test
    void testCleanupUrls() {
        Url url1 = new Url();
        Url url2 = new Url();
        List<Url> urlsPage0 = Arrays.asList(url1, url2);

        @SuppressWarnings("unchecked")
        Page<Url> page0 = mock(Page.class);
        when(page0.getContent()).thenReturn(urlsPage0);
        when(page0.hasNext()).thenReturn(true);

        @SuppressWarnings("unchecked")
        Page<Url> page1 = mock(Page.class);
        when(page1.getContent()).thenReturn(Collections.emptyList());
        when(page1.hasNext()).thenReturn(false);

        when(urlService.getPageExpiredUrls(any(LocalDateTime.class), eq(PageRequest.of(0, 100))))
                .thenReturn(page0);
        when(urlService.getPageExpiredUrls(any(LocalDateTime.class), eq(PageRequest.of(1, 100))))
                .thenReturn(page1);

        ExecutorService directExecutor = Executors.newSingleThreadExecutor();
        ReflectionTestUtils.setField(cleanerScheduler, "urlCleanupExecutor", directExecutor);

        cleanerScheduler.cleanupUrls();

        verify(cleanerService, times(1)).cleanUrlsAndSaveHashes(urlsPage0);
        verify(urlService, times(1)).getPageExpiredUrls(any(LocalDateTime.class), eq(PageRequest.of(0, 100)));
        verify(urlService, times(1)).getPageExpiredUrls(any(LocalDateTime.class), eq(PageRequest.of(1, 100)));

        directExecutor.shutdown();
    }
}
