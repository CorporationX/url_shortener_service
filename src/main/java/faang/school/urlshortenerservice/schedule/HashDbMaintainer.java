package faang.school.urlshortenerservice.schedule;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashDbMaintainer {
    private final HashDao hashDao;
    private final HashGenerator hashGenerator;

    @Value("${hash.db.low-data-mark}")
    private long dbLowDataMark;

    @Value("${hash.db.replenish-batch-size}")
    private int replenishBatchSize;

    @Scheduled(cron = "${hash.db.replenish-cron}")
    @SchedulerLock(name = "replenishDbHashes", lockAtMostFor = "${hash.db.replenish-lock-at-most-for}")
    public void replenishDbHashes() {
        log.info("Checking DB hash supply");
        long hashes = hashDao.countHashes();
        if (hashes < dbLowDataMark) {
            log.info("DB hash supply is low. Attempting to generate and store {} new hashes.",
                    replenishBatchSize);
            hashGenerator.generateBatch(replenishBatchSize);
            log.info("Successfully generated and stored {} new hashes in the database.",
                    replenishBatchSize);
        } else {
            log.info("Db hash supply is high. Skipping replenishment.");
        }
    }
}
