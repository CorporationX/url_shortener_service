package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    UrlService urlService;

    @Transactional
    @Scheduled(cron = "${url.cleaner.scheduler}")
    public void cleanOldUrl(){
        urlService.deleteOldUrls();
    }
}