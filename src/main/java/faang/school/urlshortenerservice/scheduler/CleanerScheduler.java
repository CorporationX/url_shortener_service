package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.Hash.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor

public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${hash.scheduler.cleanOldUrls.fixed-rate}")
    public void cleanOldUrls() {
        LocalDateTime yearAgo = LocalDateTime.now().minusYears(1);

        List<String> oldHashes = urlRepository.deleteOldUrls(yearAgo);

        if (!oldHashes.isEmpty()) {
            hashRepository.save(oldHashes);
        }
    }
}
