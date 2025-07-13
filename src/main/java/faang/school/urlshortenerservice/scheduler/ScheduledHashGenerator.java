package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.generator.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledHashGenerator {
    HashGenerator hashGenerator;

    @Scheduled(cron = "${hashRange.scheduler_cron}")
    public void generateHash() {
        hashGenerator.generateHash();
    }
}
