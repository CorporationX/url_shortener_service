package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.HashGenerator;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeneratorHashScheduler {

    private final UrlService urlService;
    private final HashGenerator hashGenerator;

    @Value("${app.cleaner.interval:10}")
    private int interval;
    @Value("${app.generator.max_amount:10}")
    private Long hashRange;

    @Scheduled(cron = "${app.cleaner.cron.expression}")
    public void doCleaner() {
        log.info("Cleaner old hashes started");
        urlService.clearOldUrls(interval);
    }

    @Scheduled(cron = "${app.generator.cron.expression}")
    public void generateBatchBySchedule() {
        log.info("Generate batch urls started");
        hashGenerator.generateBatchBySchedule(hashRange);
    }
}
