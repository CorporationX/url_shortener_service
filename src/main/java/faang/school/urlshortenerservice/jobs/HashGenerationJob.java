package faang.school.urlshortenerservice.jobs;

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

    private static final int MIN_HASHES = 100;
    private static final int BATCH_SIZE = 500;

    @Scheduled(fixedDelayString = "${hash.generator.job.delay:60000}")
    public void generateHashesIfNeeded() {
        long count = hashRepository.count();
        log.info("Current hashes in DB: {}", count);

        if (count < MIN_HASHES) {
            int toGenerate = BATCH_SIZE;
            log.info("Hashes below threshold ({}). Generating {} new hashes...", MIN_HASHES, toGenerate);
            hashGeneratorService.generateHashes(toGenerate);
            log.info("Hash generation completed");
        }
    }
}
