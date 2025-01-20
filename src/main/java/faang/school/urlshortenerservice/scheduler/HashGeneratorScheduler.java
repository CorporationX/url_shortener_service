package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.generator.HashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class HashGeneratorScheduler {
    private final HashGenerator hashGenerator;


    @Scheduled(cron = "${hash.generator.scheduled.cron}")
    public void generateHashes() {
        hashGenerator.generateHashList();
    }
}
