package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${hash.cleaner.days-before-obsolete}")
    private int daysBeforeObsolete;

    @Scheduled(cron = "${hash.cleaner.cron}")
    public void cleanObsoleteHashes() {
        log.info("Starting obsolete hash cleaning job.");
        LocalDateTime expirationDate = LocalDateTime.now().minusDays(daysBeforeObsolete);

        List<String> hashesToReuse = urlRepository.deleteAllByCreatedAtBefore(expirationDate);
        log.debug("Found {} hashes created before {}", hashesToReuse.size(), expirationDate);

        if (!hashesToReuse.isEmpty()) {
            List<Hash> newHashesForReuse = hashesToReuse.stream()
                    .map(Hash::new)
                    .toList();
            hashRepository.saveAll(newHashesForReuse);
            log.info("Saved {} hashes for reuse", newHashesForReuse.size());
        } else {
            log.info("No obsolete hashes found for reuse.");
        }

        log.info("Obsolete hash cleaning job finished.");
    }
}
