package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    @Scheduled(cron = "${schedulers.crons.cron-for-clean-urls}")
    public void clean() {
        List<String> hashes = urlRepository.getAllForClean();
        if (hashes != null && !hashes.isEmpty()) {
            hashRepository.save(hashes);
        }
    }
}
