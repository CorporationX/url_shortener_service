package faang.school.urlshortenerservice.service.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
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

    @Scheduled(cron = "${app.hash.cleaner.scheduler.cron}")
    @Transactional
    public void cleanOldUrls() {
        List<String> freedHashes = urlRepository.deleteOldUrls();
        if (!freedHashes.isEmpty()) {
            hashRepository.saveAll(
                    freedHashes.stream()
                            .map(hashString ->
                                    Hash.builder()
                                            .hash(hashString)
                                            .build()
                            )
                            .toList()
            );
        }
    }
}
