package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlRepository urlRepository;

    @Value("${spring.scheduler.hash-url-cleaner.couple-expiration-in-days}")
    private int expirationDays;

    @Transactional
    @Scheduled(cron = "${spring.scheduler.hash-url-cleaner.cron}")
    public void clean() {
        OffsetDateTime threshold = OffsetDateTime.now().minusDays(expirationDays);
        List<Url> needToDelete = urlRepository.findUrlsByCreatedAtBefore(threshold);
        urlRepository.deleteAll(needToDelete);
    }
}
