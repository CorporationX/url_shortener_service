package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class Scheduler {
    private final AtomicInteger generateCountMessage = new AtomicInteger();
    private final UrlRepository urlRepository;
    private final HashGenerator hashGenerator;

    @Value(value = "${hash.time.saving.day:100}")
    private int savingDays;

    public void sendMessage() {
        generateCountMessage.incrementAndGet();
    }

    @Scheduled(cron = "${scheduled.generate-cron: 0 0 0 * * *}")
    public void deleteUnusedUrl() {
        urlRepository.deleteUnusedUrl(LocalDateTime.now().minusDays(savingDays));
    }

    @Scheduled(cron = "${scheduled.generate-cron: 10 * * * * *}")
    public void checkGenerateMessage() {
        if (generateCountMessage.get() > 0) {
            hashGenerator.generateHash();
            generateCountMessage.set(0);
        }
    }
}
