package faang.school.urlshortenerservice.job;

import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GenerateHashJob {
    private final HashService hashService;

    @Scheduled(cron = "${app.job.hash.generate.cron:* * * * * *}")
    public void generateHash() {
        hashService.generateHashBatchIfNeeded();
    }
}
