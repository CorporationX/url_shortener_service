package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FreeUnusedHashScheduler {
    private final UrlService urlService;

    @Async("freeUnusedHashPool")
    @Scheduled(cron = "${scheduler.free-hash-cron}")
    public void freeUnusedHash() {
        urlService.freeUnusedHash();
    }

}
