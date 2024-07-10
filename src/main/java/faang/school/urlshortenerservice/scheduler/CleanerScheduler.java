package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.jpa.HashRepository;
import faang.school.urlshortenerservice.repository.jpa.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Transactional
    @Scheduled(cron = "${services.url.cleaner.cron}")
    public void cleanOldUrls() {

        LocalDateTime fromDate = LocalDateTime.now().minusYears(1L);
        List<String> hashes = urlRepository.removeOldAndGetHashes(fromDate);

        if (!hashes.isEmpty()) {
            hashRepository.saveAll(hashes);
        }
    }
}
