package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.config.ConstantsProperties;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.ExpiredHashCleanerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpiredUrlsHashCleanScheduler {
    private final ConstantsProperties constantsProperties;
    private final ExpiredHashCleanerService hashCleanerService;
    private final UrlRepository urlRepository;

    @Scheduled(cron = "${scheduling.cron.expired_hash_cleanup}")
    @SchedulerLock(
            name = "${scheduling.lock.url_cleaner_lock_name}",
            lockAtLeastFor = "${scheduling.lock.at_most_time}"
    )
    @Async("taskExecutor")
    public void cleanUpHashes() {
        Long expiredNumber = urlRepository.countExpired(constantsProperties.getExpirationInterval());
        log.info("Expired links cleanup started for {} hashes", expiredNumber);
        int cycleCounter = 0;

        int cycleCleaned;
        do {
            cycleCleaned = hashCleanerService.cleanUpBatch();
            cycleCounter++;
        } while (cycleCleaned == constantsProperties.getCleanUpBatchSize());

        long cleanedByInstance = (long) (cycleCounter - 1) * constantsProperties.getCleanUpBatchSize() + cycleCleaned;
        log.info("Expired url cleanup FINISHED, {} url cleaned with instance", cleanedByInstance);
    }
}
