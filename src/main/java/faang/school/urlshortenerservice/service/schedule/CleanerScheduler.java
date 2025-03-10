package faang.school.urlshortenerservice.service.schedule;

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

    @Scheduled(cron = "0 0 0 * * *")
    public void cleanupUrls() {
        LocalDateTime expirationDate = LocalDateTime.now().minusMonths(urlExpirationMonths);

        int pageNumber = 0;
        Page<Url> page;
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        do {
            page = urlService.getPageExpiredUrls(expirationDate, PageRequest.of(pageNumber, batchSize));
            List<Url> expiredUrls = page.getContent();

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> cleanerService.cleanUrlsAndSaveHashes(expiredUrls), urlCleanupExecutor);
            futures.add(future);
            pageNumber++;
        } while (page.hasNext());
        
        futures.forEach(CompletableFuture::join);
        log.info("URL cleanup job completed");
    }
}
