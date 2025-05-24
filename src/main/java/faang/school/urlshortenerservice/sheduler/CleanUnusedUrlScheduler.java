package faang.school.urlshortenerservice.sheduler;

import faang.school.urlshortenerservice.properties.HashCleanerProperties;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CleanUnusedUrlScheduler {
    private final UrlService urlService;

    @Scheduled(cron = "#{@hashCleanerProperties.cron}")
    public void deleteUnusedHashes(){
        urlService.deleteUnusedHashes();
    }
}