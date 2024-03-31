package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.util.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GenerateScheduler {
    private final HashGenerator hashGenerator;

    @Scheduled(cron = "@weekly")
    private void generateHashes() {
        hashGenerator.generateBatch();
    }
}
