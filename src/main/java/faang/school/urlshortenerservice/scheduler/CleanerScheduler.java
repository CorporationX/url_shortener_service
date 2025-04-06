package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    @Value("${data.clean-scheduler.date.interval}")
    private int interval;

    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;

    @Scheduled(cron = "${clean-scheduler.cron}")
    @Transactional
    public void deleteUnusedUrls() {
        List<Hash> hashes = urlRepository.deleteOldUrlsAndReturnHashes(interval);
        hashRepository.saveAll(hashes);
    }
}
