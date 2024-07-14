package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    @Value("${scheduler.clearOldHashes.period}")
    private String period;

    @Transactional
    @Async()
    @Scheduled(cron = "${scheduler.clearOldHashes.cronExpression}")
    public void clearOldHashes() {
        List<String> oldHashes = urlRepository.clearOldHashes(period);
        hashRepository.saveAll(oldHashes);
    }
}
