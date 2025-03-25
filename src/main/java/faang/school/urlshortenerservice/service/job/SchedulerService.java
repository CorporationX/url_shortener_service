package faang.school.urlshortenerservice.service.job;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SchedulerService {
    private final UrlService urlService;

    @Scheduled(cron = "${hash-generator.cron-delete}", scheduler = "schedulerExecutorService")
    public void deleteOldUrl() {
        log.info("Deleting old urls");
        long deletedCount = urlService.deleteOldUrl();
        log.info("Deleted {} urls", deletedCount);
    }


}
