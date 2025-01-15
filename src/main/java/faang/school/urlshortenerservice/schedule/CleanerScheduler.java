package faang.school.urlshortenerservice.schedule;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${scheduler.cron}")
    public void cleanOldUrls() {
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);

        List<String> hashes = urlRepository.deleteUrlsOlderThan(oneYearAgo);

        hashes.forEach(hash -> {
            hashRepository.save(new Hash(hash));
        });

        log.info("Deleted URLs older than {}", oneYearAgo);
    }
}
