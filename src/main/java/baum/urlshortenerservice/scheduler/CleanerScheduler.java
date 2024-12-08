package baum.urlshortenerservice.scheduler;

import baum.urlshortenerservice.repository.HashRepository;
import baum.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    @Value("${schedulers.url-cleaner.interval}")
    private String interval;

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${schedulers.crons.cron-for-clean-urls}")
    @Transactional
    public void clean() {
        List<String> hashes = urlRepository.getAndDeleteAllUrlsOlderInterval(interval);
        if (!hashes.isEmpty()) {
            hashRepository.save(hashes);
        }
    }
}
