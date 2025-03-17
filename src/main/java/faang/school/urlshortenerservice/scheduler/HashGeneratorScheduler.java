package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.generator.HashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGeneratorScheduler {
    private final HashGenerator hashGenerator;

    @Scheduled(cron = "${generate.cron}")
    @Transactional
    public void generateHashes() {
        log.info("Starting generating of new hashes");
        hashGenerator.generateHash();
    }
}
