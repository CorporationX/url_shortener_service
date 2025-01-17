package faang.school.urlshortenerservice.service.sheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.service.HashService;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final HashService hashService;
    private final UrlService urlService;

    @Async("cleanUrlSchedulePool")
    @Scheduled(cron = "${app.cron.clean_url}")
    public void cleanExpiredUrl() {
        List<String> expiredHashes = urlService.getExpiredHashAndDeleteUrl();
        List<Hash> hashes = expiredHashes.stream()
                .map(Hash::new)
                .toList();
        hashService.saveAll(hashes);
    }
}