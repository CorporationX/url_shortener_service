package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.url.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class Scheduler {
    private final AtomicInteger generateCountMessage = new AtomicInteger();
    private final UrlService urlService;

    public void sendMessage() {
        generateCountMessage.incrementAndGet();
    }

    //todo add executor
    @Scheduled(cron = "${scheduled.generate-cron: 0 0 0 * * *}")
    public void deleteUnusedUrl() {
        urlService.deleteUnusedUrl();
    }

}
