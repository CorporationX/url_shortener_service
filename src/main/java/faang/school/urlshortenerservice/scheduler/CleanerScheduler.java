package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class CleanerScheduler {

    private final HashRepository hashRepository;
    private final UrlRepository urlrepository;

    @Scheduled(cron = "${scheduler.cron}")
    @Transactional
    public void cleanOldUrls() {
        List<String> oldHashes = urlrepository.deleteOldUrlsAndGetHashes();
        hashRepository.save(oldHashes);
    }
}
