package faang.school.urlshortenerservice.scheduled;

import faang.school.urlshortenerservice.config.context.scheduler.hashgenerator.HashGeneratorScheduleProperties;
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

    @Scheduled(cron = "#{@hashGeneratorScheduleProperties.cron}")
    public void scheduleHashGeneration() {
        if (!properties.isEnabled()) {
            log.info("Scheduled hash generation is disabled");
            return;
        }

        log.info("Scheduled hash generation started");
        hashGenerator.generateHashesAsync();
        log.info("Scheduled hash generation finished");
    }
}