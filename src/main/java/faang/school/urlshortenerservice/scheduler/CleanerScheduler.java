package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    @Value("${hash.clean.interval.value}")
    private String interval;

    @Value("${hash.clean.interval.count}")
    private Integer count;
    private final LocalDateTime defaultInterval = LocalDateTime.now().minusYears(1);
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Async("schedulerThreadPool")
    @Scheduled(cron = "${hash.scheduler.cron}")
    @Transactional
    public void clearOldUrls() {
        List<String> hashes = urlRepository.deleteAndReturnByInterval(getDate(interval, count));
        hashRepository.saveAll(hashes);
    }
    private LocalDateTime getDate (String interval, Integer count) {
        if (count == null) {
            count = 1;
        }
        return switch (interval) {
            case "year" -> LocalDateTime.now().minusYears(count);
            case "month" -> LocalDateTime.now().minusMonths(count);
            default -> defaultInterval;
        };
    }
}