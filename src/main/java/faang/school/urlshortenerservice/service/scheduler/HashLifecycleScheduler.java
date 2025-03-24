package faang.school.urlshortenerservice.service.scheduler;

import faang.school.urlshortenerservice.entity.FreeHash;
import faang.school.urlshortenerservice.entity.UrlMapping;
import faang.school.urlshortenerservice.enums.HashStatus;
import faang.school.urlshortenerservice.repository.FreeHashRepository;
import faang.school.urlshortenerservice.repository.UrlMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashLifecycleScheduler {

    private final UrlMappingRepository urlMappingRepository;
    private final FreeHashRepository freeHashRepository;

    @Value("${shortener.waiting-period-days}")
    private int waitingPeriodDays;

    @Scheduled(cron = "${shortener.cron.update-hash-statuses}")
    @Transactional
    public void updateHashStatuses() {
        log.info("Start updating hash statuses");
        LocalDateTime now = LocalDateTime.now();

        List<UrlMapping> expired = urlMappingRepository.findByExpiredAtBefore(now);

        expired.forEach(mapping -> {
            if (mapping.getStatus() == HashStatus.ACTIVE) {
                mapping.setStatus(HashStatus.WAITING);
                log.debug("Moved {} to WAITING", mapping.getHash());
            } else if (mapping.getStatus() == HashStatus.WAITING
                    && mapping.getExpiredAt().plusDays(waitingPeriodDays).isBefore(now)) {

                mapping.setStatus(HashStatus.FREE);
                log.debug("Moved {} to FREE", mapping.getHash());
            }

        });
        log.info("Statuses updated for {} hashes", expired.size());
        urlMappingRepository.saveAll(expired);
    }

    @Scheduled(cron = "${shortener.cron.cleanup-and-free-hashes}")
    @Transactional
    public void cleanUpAndFreeHashes() {
        log.info("Start cleanup and free hashes");
        List<UrlMapping> freeMappings = urlMappingRepository.findByStatus(HashStatus.FREE);

        if (freeMappings.isEmpty()) {
            return;
        }
        List<FreeHash> freed = freeMappings.stream()
                .map(mapping -> new FreeHash(mapping.getHash()))
                .toList();
        freeHashRepository.saveAll(freed);

        urlMappingRepository.deleteAll(freeMappings);
        log.info("Moved {} FREE hashes into FreeHash table", freed.size());
    }
}
