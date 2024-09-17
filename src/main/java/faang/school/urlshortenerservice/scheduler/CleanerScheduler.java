package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${url.scheduler.cron}")
    @Transactional
    public void clearExpiredUrls(){
        List<String> releasedHashes = urlRepository.deleteExpiredUrlsReturningHashes();
        hashRepository.batchSave(releasedHashes);
    }
}
