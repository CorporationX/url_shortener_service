package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
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
        LocalDateTime date = getDate(interval, count);
        List<Hash> hashes = urlRepository.deleteAndReturnBefore(date)
                .stream().map(Hash::new).toList();
        hashRepository.saveAllInBatch(hashes);
    }

    private LocalDateTime getDate(String interval, Integer count) {
        if (count == null) {
            count = 1;
        }
        return switch (interval) {
            case "year" -> LocalDateTime.now().minusYears(count);
            case "month" -> LocalDateTime.now().minusMonths(count);
            case "week" -> LocalDateTime.now().minusWeeks(count);
            case "day" -> LocalDateTime.now().minusDays(count);
            case "sec" -> LocalDateTime.now().minusSeconds(count);
            default -> defaultInterval;
        };
    }
}