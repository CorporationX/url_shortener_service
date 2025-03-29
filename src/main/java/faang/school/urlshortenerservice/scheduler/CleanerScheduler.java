package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlServiceAsync;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlServiceAsync urlServiceAsync;

    @Scheduled(cron = "${url-shortener.scheduler.delete-old-url}")
    public void deleteOldUrl() {
        urlServiceAsync.deleteOldUrlAsync();
    }
}
