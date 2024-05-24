package faang.school.urlshortenerservice.service.scheduler;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final HashRepository hashRepository;
    private final HashCache hashCache;

    @Transactional
    @Scheduled(cron = "${scheduler.clean-hashes-cron}")
    public void cleanHashes() {
        List<String> hashes = hashRepository.cleanAndGetHashes()
                .stream()
                .map(Hash::getHash)
                .toList();
        hashCache.addHashes(hashes);
    }
}
