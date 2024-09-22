package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Period;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {
    @Value("${schedulers.cleaner.period}")
    private Period period;
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${schedulers.cleaner.cron}")
    @Transactional
    public void clean() {
        List<String> hashes = urlRepository.getUnusedHashesForPeriod(period);
        log.info("Gathered all hashes unused longer than {} ", period);
        hashRepository.saveHashes(hashes);
        log.info("save a batch of unused hashes created {} ago", period);
    }
}
