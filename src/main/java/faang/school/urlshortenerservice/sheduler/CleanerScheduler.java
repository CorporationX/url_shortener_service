package faang.school.urlshortenerservice.sheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    @Value("${url.cleaner.interval: 1 year}")
    private String cleanupInterval;

    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;

    @Transactional
    @Scheduled(cron = "${url.cleaner.scheduler.cron}")
    public void cleanOldUrls() {
        List<Hash> hashes = urlRepository.deleteOldUrlsAndReturnHashes(cleanupInterval);
        hashRepository.saveAll(hashes);
    }


}
