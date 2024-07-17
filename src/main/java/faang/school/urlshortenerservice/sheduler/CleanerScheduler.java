package faang.school.urlshortenerservice.sheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Async("getAsyncExecutor")
    @Transactional
    @Scheduled(cron = "${scheduler.cleaner.cron}")
    public void cleanAndAddOldHashes() {
        List<String> oldHashes = urlRepository.deleteAndGetOldHashes();
        hashRepository.save(oldHashes);
        log.info("Cleanup of the old hashes has been completed");
    }
}
