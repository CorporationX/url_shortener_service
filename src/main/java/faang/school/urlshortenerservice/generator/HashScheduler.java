package faang.school.urlshortenerservice.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HashScheduler {

    private final HashGenerator hashGenerator;

    @Scheduled(cron = "${hash.cron:0 0 0 * * *}")
    public void scheduleHashGeneration() {
        hashGenerator.generateHash();
    }
}