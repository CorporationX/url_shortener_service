package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;

    @Scheduled(cron = "${scheduler.cron}")
    @Transactional
    public void clearHashes() {
        List<String> deletedHashes = urlRepository.getExpiredHashes();
        hashRepository.save(deletedHashes);
        log.info("Cleared and resaved {} hashes", deletedHashes.size());
    }
}
