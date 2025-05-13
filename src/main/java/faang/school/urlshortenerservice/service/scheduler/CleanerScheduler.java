package faang.school.urlshortenerservice.service.scheduler;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.service.CleanerService;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final ExecutorService urlCleanupExecutor;
    private final CleanerService cleanerService;
    private final UrlService urlService;

    @Value("${url.cleanup.batch-size:100}")
    private int batchSize;

    @Value("${url.expiration.months:1}")
    private int urlExpirationMonths;

    @Scheduled(cron = "${url.cleanup.cron:0 0 0 * * *}")
    public void cleanupUrls() {
        LocalDateTime expirationDate = LocalDateTime.now().minusMonths(urlExpirationMonths);
        log.info("CleanerScheduler: Starting cleanup of URLs expired before {}", expirationDate);

        int pageNumber = 0;
        Page<Url> page;
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        do {
            page = urlService.getPageExpiredUrls(expirationDate, PageRequest.of(pageNumber, batchSize));
            List<Url> expiredUrls = page.getContent();
            if (!expiredUrls.isEmpty()) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
                                cleanerService.cleanUrlsAndSaveHashes(expiredUrls),
                        urlCleanupExecutor);
                futures.add(future);
            }
            pageNumber++;
        } while (page.hasNext());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        log.info("CleanerScheduler: URL cleanup job completed");
    }
}
