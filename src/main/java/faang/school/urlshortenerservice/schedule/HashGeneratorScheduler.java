package faang.school.urlshortenerservice.schedule;

import faang.school.urlshortenerservice.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HashGeneratorScheduler {
    private final HashGenerator hashGenerator;
    @Value("${uniqueIdsPerBatch}")
    private int uniqueIdsPerBatch;

    @Async("generatorExecutor")
    @Scheduled(cron = "${hash.schedule-cron}")
    public void schedule() {
        hashGenerator.generateBatch(uniqueIdsPerBatch);
    }
}
