package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
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

    @Scheduled(cron = "${cleaner.cron.expression}")
    @Transactional
    public void cleanOldUrlsAndRestoreHashes() {
        List<String> expiredHashes = urlRepository.deleteUrlsOlderThanOneYear();
        saveHashesIfNotEmpty(expiredHashes);
    }

    private void saveHashesIfNotEmpty(List<String> hashes) {
        if (!hashes.isEmpty()) {
            List<Hash> hashEntities = createHashEntities(hashes);
            hashRepository.saveAll(hashEntities);
        }
    }

    private List<Hash> createHashEntities(List<String> hashes) {
        return hashes.stream()
                .map(Hash::new)
                .toList();
    }

}
