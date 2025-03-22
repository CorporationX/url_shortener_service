package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.config.SchedulerProperties;
import faang.school.urlshortenerservice.generator.HashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGeneratorScheduler {
    private final HashGenerator hashGenerator;
    private final SchedulerProperties properties;

    @Scheduled(cron = "${hash.generator.scheduler.cron}")
    @Async("hashGeneratorExecutorForScheduler")
    public void generateHashes() {
        log.info("Starting generating of new hashes");
        try {
            hashGenerator.generateHash(properties.getQuantity());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

