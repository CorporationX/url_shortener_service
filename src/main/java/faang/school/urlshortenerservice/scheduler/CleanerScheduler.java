package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;

    @Scheduled(cron = "${scheduler.cron.expression}")
    @Transactional
    public void cleanOldUrls() {
        urlRepository.deleteOldUrlsAndRecycleHashes();
    }
}
