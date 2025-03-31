package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${scheduler.cleaner.cron}")
    @Transactional
    public void cleanOldAssociations() {
        List<String> hashes = urlRepository.deleteOldUrlsAndReturnHashes();
        if (!hashes.isEmpty()) {
            hashRepository.saveAllHashes(hashes);
        }
    }
}
