package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${scheduler.clear.daysAgo}")
    private int daysAgo;

    @Transactional
    @Async
    @Scheduled(cron = "${scheduler.clear.cronExpression}")
    public void clearOldHashes() {
        List<String> oldHashes = urlRepository.clearOldHashes(daysAgo);

        List<Hash> hashesToRecycle = oldHashes.stream()
                .map(hash -> Hash.builder().hash(hash).build())
                .collect(Collectors.toList());
        if (!hashesToRecycle.isEmpty()) {
            hashRepository.save(hashesToRecycle.stream().map(Hash::getHash).toList());
        }
    }
}
