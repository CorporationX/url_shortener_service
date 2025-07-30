package faang.school.urlshortenerservice.job;


import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExpiredUrlCleaningJob {

    private final UrlService urlService;

    @Scheduled(cron = "${app.cleaning-job.cron}")
    public void cleanExpiredUrls() {
        urlService.deleteExpiredUrls();
    }
}
