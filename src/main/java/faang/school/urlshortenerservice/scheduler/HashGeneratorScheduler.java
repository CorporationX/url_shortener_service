package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.generator.HashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGeneratorScheduler {
    private final HashGenerator hashGenerator;

    @Value("${hash.generator.batch-size}")
    private int batchSize;

    @Scheduled(cron = "${hash.generator.scheduled.cron}")
    public void generateHashes() {
        hashGenerator.generateAndSaveBatch(batchSize);
        log.info("Generated and saved {} new hashes.", batchSize);
    }
}
