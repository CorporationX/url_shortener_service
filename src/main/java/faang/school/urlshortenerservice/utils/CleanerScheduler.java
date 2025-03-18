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
        List<Url> urls = urlRepository.findAll();
        List<Url> needToDelete = urls.stream().filter(url ->
                url.getCreated_at().isBefore(OffsetDateTime.now().minusDays(expirationDays))).toList();
        urlRepository.deleteAll(needToDelete);
    }
}
