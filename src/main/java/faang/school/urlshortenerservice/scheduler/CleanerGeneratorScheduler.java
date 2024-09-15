package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CleanerGeneratorScheduler {
    private final UrlService urlService;
    private final HashGenerator hashGenerator;
    @Value("${generator.scheduled.removedPeriod}")
    private String removedPeriod;


    @Scheduled(cron = "${generator.delete.url.scheduled.cron}")
    public void deleteOldURL() {
        urlService.deleteOldURL(removedPeriod);
    }

    @Scheduled(cron = "${generator.scheduled.cron}")
    public void generateBatch(){
        hashGenerator.generateAndSaveBatch();
    }
}