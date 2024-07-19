package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
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

    @Scheduled(cron = "${hash-service.scheduler.cron}")
    @Transactional
    public void cleanUrls() {
        LocalDateTime dateDelete = LocalDateTime.now().minusYears(1);
        List<Hash> hashes = urlRepository.deleteOldUrlAndReturnHash(dateDelete);

        if (!hashes.isEmpty()) {
            hashRepository.saveAll(hashes);
        }
    }
}
