package faang.school.urlshortenerservice.scheduler.shorter;


import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShorterCleaner {

    private final UrlService urlService;

    @Async("shorterCleanerExecutor")
    public void cleanExpiredUrlsBatchAsync(int limit) {
        long start = System.currentTimeMillis();
        try {
           urlService.deleteExpiredShortUrls(limit);
           log.info("Batch deleted in {} millis.", (System.currentTimeMillis() - start));
        } catch (Exception ex) {
            log.error("Failed to delete batch", ex);
        }
    }
}