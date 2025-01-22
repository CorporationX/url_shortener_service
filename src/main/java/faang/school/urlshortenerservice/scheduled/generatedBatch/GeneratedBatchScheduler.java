package faang.school.urlshortenerservice.scheduled.generatedBatch;

import faang.school.urlshortenerservice.generator.HashGenerator;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GeneratedBatchScheduler {

    private final HashGenerator hashGenerator;

    @Scheduled(cron = "${scheduled.cron_generated}")
    public void generateBatch() {
        hashGenerator.generateBatch();
    }

}
