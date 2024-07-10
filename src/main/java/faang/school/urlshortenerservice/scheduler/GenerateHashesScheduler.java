package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.hash.HashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenerateHashesScheduler {

    private final HashService hashService;

    @Scheduled(cron = "${scheduler.generate.cron}")
    public void generateHashes() {
        hashService.generateHashes();
    }
}
