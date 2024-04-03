package faang.school.urlshortenerservice.sheduler;

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
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${url.to-be-deleted.interval:1 year}")
    private String interval;

    @Transactional
    @Scheduled(cron = "${hash.cron.expression:0 0 0 1 * *}")
    public void cleanOldHashes() {
        List<String> urlHashes = urlRepository.deleteUrl(interval);
        List<Hash> hashes = urlHashes.stream()
                .map(Hash::new)
                .toList();

        hashRepository.saveAll(hashes);
    }
}
