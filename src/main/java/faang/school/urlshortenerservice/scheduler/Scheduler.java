package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class Scheduler {
    private final AtomicInteger generateCountMessage = new AtomicInteger();
    private final UrlService urlService;
    private final HashGenerator hashGenerator;

    public void sendMessage() {
        generateCountMessage.incrementAndGet();
    }

    @Scheduled(cron = "${scheduled.generate-cron: 0 0 0 * * *}")
    public void deleteUnusedUrl() {
        urlService.deleteUnusedUrl();
    }

    @Scheduled(cron = "${scheduled.generate-cron: 10 * * * * *}")
    public void checkGenerateMessage() {
        if (generateCountMessage.get() > 0) {
            hashGenerator.generateHash();
            generateCountMessage.set(0);
        }
    }
}
