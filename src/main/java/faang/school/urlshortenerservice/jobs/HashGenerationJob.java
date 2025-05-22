package faang.school.urlshortenerservice.jobs;

import faang.school.urlshortenerservice.config.HashGenerationJobProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerationJob {

    private final HashRepository hashRepository;
    private final HashGeneratorService hashGeneratorService;
    private final HashGenerationJobProperties properties;

    @Scheduled(fixedDelayString = "${hash.generator.job.delay:60000}")
    public void generateHashesIfNeeded() {
        long count = hashRepository.count();
        log.debug("Current hashes in DB: {}", count);

        if (count < properties.getMinHashes()) {
            int toGenerate = properties.getBatchSize();
            log.info("Hashes below threshold ({}). Generating {} new hashes...", properties.getMinHashes(), toGenerate);
            hashGeneratorService.generateHashes(toGenerate);
            log.info("Hash generation completed");
        }
    }
}
