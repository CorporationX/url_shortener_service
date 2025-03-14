package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Transactional
    @Scheduled(cron = "${url.cleaner.cron}")
    public void cleanOldUrl() {
        log.info("Start cleaning old url...");
        List<String> hashes = urlRepository.deleteOldUrlAndReturnHashes();
        log.info("Deleted old url");
        hashRepository.save(hashes);
        log.info("Saved hashes");
    }
}
