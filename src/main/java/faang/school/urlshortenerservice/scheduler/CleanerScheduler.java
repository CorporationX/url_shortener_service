package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    @PersistenceContext
    private final EntityManager entityManager;

    @Scheduled(cron = "${clean.cron}")
    @Transactional
    public void cleanExpiredUrls() {
        log.info("Starting clean expired urls");
        List<Hash> freeHashes = urlRepository.findHashesWithExpiredDates(LocalDateTime.now().minusYears(1));
        if ((freeHashes != null) && (!freeHashes.isEmpty())) {
            freeHashes.forEach(entityManager::persist);
            entityManager.flush();
        }
    }
}


