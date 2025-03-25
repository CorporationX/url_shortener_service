package faang.school.urlshortenerservice.scheduled;

import faang.school.urlshortenerservice.config.shortener.ShortenerProperties;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.service.HashService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
public class HashGenerator {

    private final HashService hashService;
    private final int numberOfHashes;

    public HashGenerator(ShortenerProperties shortenerProperties, HashService hashService) {
        this.hashService = hashService;
        this.numberOfHashes = shortenerProperties.hashesBatchSize() * shortenerProperties.multiplier();
    }

    @Scheduled(cron = "${shortener.schedule.hash-cron}")
    @Transactional
    public void generateAndSaveHashes() {
        log.info("Generation {} of hashes to database...", numberOfHashes);
        List<Hash> hashes = hashService.generateHashes(numberOfHashes);
        hashService.saveHashes(hashes);
        log.info("Number of generated hashes : {}", hashes.size());
    }
}
