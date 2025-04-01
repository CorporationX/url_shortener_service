package faang.school.urlshortenerservice.service.scheduler;

import faang.school.urlshortenerservice.entity.FreeHash;
import faang.school.urlshortenerservice.entity.UrlMapping;
import faang.school.urlshortenerservice.repository.FreeHashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashLifecycleScheduler {

    private final UrlRepository urlRepository;
    private final FreeHashRepository freeHashRepository;

    @Scheduled(cron = "${shortener.cron.cleanup-and-free-hashes}")
    @Transactional
    public void cleanUpAndFreeHashes() {
        log.info("Start cleanup and free hashes");
        List<UrlMapping> freeMappings = urlRepository.findByExpiredAtBefore(LocalDateTime.now());

        if (freeMappings.isEmpty()) {
            return;
        }
        List<FreeHash> freed = freeMappings.stream()
                .map(mapping -> new FreeHash(mapping.getHash()))
                .toList();
        freeHashRepository.saveAll(freed);

        urlRepository.deleteAll(freeMappings);
        log.info("Moved {} FREE hashes into FreeHash table", freed.size());
    }
}
