package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${scheduler.cron.expression}")
    @Transactional
    public void cleanupOldUrl() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);

        urlRepository.deleteOldUrls(oneYearAgo).forEach(hash -> hashRepository.save(new Hash(hash)));
    }
}
