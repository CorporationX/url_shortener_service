package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Transactional
    @Scheduled(cron = "${scheduling.cron.cleaner}")
    public void deleteOldUrlAssociations() {
        List<String> oldHashes = urlRepository.deleteUrlAssociationByTime();
        if (!oldHashes.isEmpty()) {
            hashRepository.saveAll(oldHashes.stream().map(Hash::new).toList());
        }
    }
}
