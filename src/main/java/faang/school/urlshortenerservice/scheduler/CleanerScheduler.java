package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.service.HashService;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlService urlService;
    private final HashService hashService;

    @Scheduled(cron = "${scheduler.cleaner.cron}")
    @Async
    public void cleanHash() {
        List<Url> oldUrls = urlService.findUrlsCreatedBefore(LocalDateTime.now().minusYears(1));
        List<String> hashesToSave = oldUrls.stream()
                .map(Url::getHash)
                .toList();
        hashService.saveAllHashes(hashesToSave);
        urlService.deleteAll(oldUrls);
    }
}
