package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlService urlService;

    @Retryable(maxAttemptsExpression = "${spring.retry.max-attempts}", backoff = @Backoff(multiplierExpression = "${spring.retry.backoff.multiplier}"))
    @Scheduled(cron = "${scheduled.cron.clean-old-urls}")
    public void cleanOldUrls() {
        log.info("Starting job to clean old urls");
        urlService.deleteOldShortUrls();
    }
}
