package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.hash.HashGenerator;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SchedulerService {

    private final HashGenerator hashGenerator;
    private final UrlService urlService;

    @Scheduled(cron = "${scheduled.cron.every-day}")
    public void scheduleGeneratorBatch() {
        hashGenerator.generateBatch();
    }

    @Scheduled(cron = "${scheduled.cron.every-day}")
    public void clearOldUrls() {
        urlService.loadingFreeHashFromDb(urlService.clearOldUrls());
    }
}
