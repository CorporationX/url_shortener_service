package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.service.HashService;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpiredUrlsRemover {
    private final UrlService urlService;
    private final HashService hashService;

    @Value("${scheduler.url-expiration-period-months}")
    private int expirationPeriodMonths;

    @Transactional
    @Scheduled(cron = "${scheduler.delete-expired-urls-cron}")
    public void deleteExpiredUrls() {
        log.info("Deleting expired urls started");
        LocalDateTime expirationDate = LocalDateTime.now().minusMonths(expirationPeriodMonths);
        List<Hash> freedHashes = urlService.deleteExpiredLinks(expirationDate);

        if (freedHashes.isEmpty()) {
            log.info("Expired urls to delete not found.");
            return;
        }

        hashService.saveHashes(freedHashes);
        log.info("Deleted {} expired urls.", freedHashes.size());
    }
}
