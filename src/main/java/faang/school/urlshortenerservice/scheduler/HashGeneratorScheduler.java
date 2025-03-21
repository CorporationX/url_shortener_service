package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.config.HashBatchProperties;
import faang.school.urlshortenerservice.generator.HashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGeneratorScheduler {
    private final HashGenerator hashGenerator;
    private final HashBatchProperties properties;

    @Scheduled(cron = "${generate.cron}")
    @Transactional
    @Async("hashGeneratorExecutorForScheduler")
    public void generateHashes() {
        log.info("Starting generating of new hashes");
        try {
            hashGenerator.generateHash(properties.getBatchSize());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

