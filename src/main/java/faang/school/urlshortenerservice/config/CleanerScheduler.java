package faang.school.urlshortenerservice.config;

import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;

    @Async()
    @Scheduled(cron = "${spring.clean.cache.scheduler.cron}")
    public void cleanCache() {
        int butchSize = 100;
        int offset = 0;
        urlRepository.reuseOldHashes(butchSize, offset);
    }
}
