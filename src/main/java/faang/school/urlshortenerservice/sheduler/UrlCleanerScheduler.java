package faang.school.urlshortenerservice.sheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class UrlCleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    @Value("${cleaner-scheduler.interval:1 year}")
    private final String interval;

    @Transactional
    @Scheduled(cron = "${cleaner-scheduler.cron:0 0 * * *}")
    public void clean() {
        List<Hash> hashes = urlRepository.getExpiredHashes(interval);
        if (!hashes.isEmpty()) {
            hashRepository.saveAll(hashes);
        }
    }
}
