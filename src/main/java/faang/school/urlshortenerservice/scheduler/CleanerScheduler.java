package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.db.HashRepository;
import faang.school.urlshortenerservice.repository.db.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${app.cleaner-scheduler.cron:0 0 0 * * *}")
    @Transactional
    public void releaseHashes() {
        log.info("Start scheduled task to release hashes older than 1 year");
        LocalDateTime nowMinusYear = LocalDateTime.now().minusYears(1);
        List<String> hashes = urlRepository.pollBefore(nowMinusYear);
        hashRepository.saveBatch(hashes);
        log.info("{} hashes was released", hashes.size());
    }
}
