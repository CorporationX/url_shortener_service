package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${cleaner.interval}")
    private String interval;

    @Scheduled(cron = "${cleaner.cron}")
    @Transactional
    public void cleanOldUrls() {
        List<String> hashes = urlRepository.deleteOldUrlsAndReturnHashes(interval);
        if (!hashes.isEmpty()) {
            hashRepository.save(hashes);
        }
    }
}
