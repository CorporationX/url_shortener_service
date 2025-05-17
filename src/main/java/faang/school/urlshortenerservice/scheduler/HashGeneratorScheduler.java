package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.HashGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGeneratorScheduler {

    private final HashGeneratorService hashGeneratorService;

    @Async("schedulerCustom")
    @Scheduled(cron = "${scheduler-setting.hash-created}")
    public void createHashesAsync() {
        log.debug("Generate hashes async started at {}", LocalDateTime.now());
        hashGeneratorService.generateHashes();
        log.debug("Generate hashes async finished at {}", LocalDateTime.now());
    }
}
