package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.generator.HashGeneratorImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledHashGenerator {
    HashGeneratorImpl hashGenerator;

    @Scheduled(cron = "${hashRange.scheduler_cron}")
    public void generateHash() {
        hashGenerator.generateHash();
    }
}
