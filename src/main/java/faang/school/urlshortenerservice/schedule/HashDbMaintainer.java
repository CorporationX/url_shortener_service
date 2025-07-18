package faang.school.urlshortenerservice.schedule;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashDbMaintainer {
    private static final int HASH_REPLENISH_LOCK_KEY = 1;

    private final HashDao hashDao;
    private final HashGenerator hashGenerator;

    @Value("${hash.db.low-data-mark}")
    private long dbLowDataMark;

    @Value("${hash.db.replenish-batch-size}")
    private int replenishBatchSize;

    @Scheduled(cron = "${hash.db.replenish-cron}")
    public void replenishDbHashes() {
        log.info("Attempting to acquire lock for db hash replenishment check.");
        if (hashDao.tryLock(HASH_REPLENISH_LOCK_KEY)) {
            try {
                log.info("Acquire lock. Checking db hash supply.");
                long hashes = hashDao.countHashes();
                if (hashes < dbLowDataMark) {
                    log.info("Db hash supply is low. Attempting to generate and store {} new hashes.",
                            replenishBatchSize);
                    hashGenerator.generateBatch(replenishBatchSize);
                    log.info("Successfully generated and stored {} new hashes in the database.",
                            replenishBatchSize);
                } else {
                    log.info("Db hash supply is high. Skipping replenishment.");
                }
            } finally {
                hashDao.unlock(HASH_REPLENISH_LOCK_KEY);
                log.info("Released database hash replenishment lock.");
            }
        } else {
            log.info("Could not acquire lock. Another instance is likely handling the check.");
        }
    }
}
