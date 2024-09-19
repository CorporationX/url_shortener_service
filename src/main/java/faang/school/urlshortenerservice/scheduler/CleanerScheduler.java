package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.db.HashRepository;
import faang.school.urlshortenerservice.repository.db.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    private final Period period;

    @Autowired
    public CleanerScheduler(UrlRepository urlRepository, HashRepository hashRepository,
                            @Value("${app.cleaner-scheduler.period}") String textPeriod) {
        this.urlRepository = urlRepository;
        this.hashRepository = hashRepository;
        this.period = Period.parse(textPeriod);
    }

    @Scheduled(cron = "${app.cleaner-scheduler.cron:0 0 0 * * *}")
    @Transactional
    public void releaseHashes() {
        log.info("Scheduled task to release was started");
        LocalDateTime stampToCleanBefore = LocalDateTime.now().minus(period);
        List<String> hashes = urlRepository.pollBeforeStamp(stampToCleanBefore);
        hashRepository.saveBatch(hashes);
        log.info("{} hashes was released", hashes.size());
    }
}
