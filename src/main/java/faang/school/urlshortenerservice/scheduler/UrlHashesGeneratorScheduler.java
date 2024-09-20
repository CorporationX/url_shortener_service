package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UrlHashesGeneratorScheduler {
    private final HashService hashService;

    @Scheduled(cron = "${spring.hash-generator.generation-cron}")
    public void generateHashes() {
        hashService.fillHashesIfNecessary();
    }
}
