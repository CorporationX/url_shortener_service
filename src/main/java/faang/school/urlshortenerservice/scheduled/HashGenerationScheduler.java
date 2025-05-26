package faang.school.urlshortenerservice.scheduled;

import faang.school.urlshortenerservice.config.scheduler.HashGeneratorScheduleProperties;
import faang.school.urlshortenerservice.exception.SchedulerException;
import faang.school.urlshortenerservice.generator.HashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerationScheduler {

    private final HashGenerator hashGenerator;
    private final HashGeneratorScheduleProperties properties;

    @Scheduled(cron = "${scheduling.hash-generation.cron}")
    public void scheduleHashGeneration() {
        if (!properties.isEnabled()) {
            log.debug("Scheduled hash generation is disabled");
            return;
        }

        try {
            log.info("Scheduled hash generation started");
            hashGenerator.generateHashesAsync();
            log.info("Scheduled hash generation finished");
        } catch (Exception e) {
            log.error("Error during scheduled hash generation", e);
            throw new SchedulerException("Failed to execute scheduled hash generation", e);
        }
    }
}