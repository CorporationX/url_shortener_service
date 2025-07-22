package faang.school.urlshortenerservice.moderation;

import faang.school.urlshortenerservice.config.moderation.UrlModerationConfiguration;
import faang.school.urlshortenerservice.service.moderation.UrlModerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@Component
@Slf4j
public class UrlModerationJob {

    private final UrlModerationService urlModerationService;
    private final ThreadPoolTaskExecutor executor;
    private final Integer batchSize;

    public UrlModerationJob(UrlModerationService urlModerationService,
                            @Qualifier("taskExecutor") ThreadPoolTaskExecutor executor,
                            UrlModerationConfiguration configuration) {
        this.urlModerationService = urlModerationService;
        this.executor = executor;
        this.batchSize = configuration.getBatchSize();
    }

    @Scheduled(cron = "#{@urlModerationConfiguration.cron}")
    public void delete() {
        int totalCount = urlModerationService.countOldUrls();
        int totalBatches = (int) Math.ceil((double) totalCount / batchSize);

        log.info("Starts cleaning the url table and saving hashes to the Hash table");
        IntStream.rangeClosed(1, totalBatches)
                .forEach(i -> executor.submit(()-> urlModerationService.deleteUrlOlderOneYearAndSaveByHash(batchSize)));
    }
}
