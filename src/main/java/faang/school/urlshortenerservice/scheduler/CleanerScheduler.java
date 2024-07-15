package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.hash.HashFreeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final HashFreeService hashFreeService;

    @Async("scheduler")
    @Scheduled(cron = "${cron.scheduler}")
    public void moderateHash() {
        log.info("Starting hash moderation process...");
        hashFreeService.moderateHash();
        log.info("Hash moderation process completed.");
    }
}
