package faang.school.urlshortenerservice.scheduler.clean;


import faang.school.urlshortenerservice.service.UrlService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Data
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlService urlService;

    @Scheduled(cron = "${spring.scheduler.cron-for-cleaner}")
    public void cleaningOldHashes() {
        urlService.cleaningOldHashes();
    }
}
