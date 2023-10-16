package faang.school.urlshortenerservice.sheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${cleaner_scheduler.period}")
    private long period;

    @Scheduled(cron = "${cleaner_scheduler.cron}")
    public void clean() {
        log.info("CleanerScheduler started in {}", System.currentTimeMillis());

        LocalDateTime minusDays = LocalDateTime.now().minusDays(period);

        List<Hash> hashes = urlRepository.cleanOldUrl(minusDays).stream()
                .map(url -> new Hash(url.getHash()))
                .toList();

        hashRepository.save(hashes);

        log.info("CleanerScheduler finished in {}", System.currentTimeMillis());
    }
}
