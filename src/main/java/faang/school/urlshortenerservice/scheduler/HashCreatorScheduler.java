package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.service.HashServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HashCreatorScheduler {

    private final HashServiceImpl hashService;
    private final HashGenerator hashGenerator;

    @Scheduled(cron = "${hash.cron}")
    public void generateHashes() {
        hashService.saveHashes(hashGenerator.generateBatch());
    }
}
