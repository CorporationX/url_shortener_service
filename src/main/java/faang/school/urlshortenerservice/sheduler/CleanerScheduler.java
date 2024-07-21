package faang.school.urlshortenerservice.sheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {

    private final HashRepository hashRepository;
    private final EntityManager entityManager;

    @Value("${scheduler.cleaner.interval}")
    private String interval;

    @Async("getAsyncExecutor")
    @Transactional
    @Scheduled(cron = "${scheduler.cleaner.cron}")
    public void cleanAndAddOldHashes() {
        String sql = "DELETE FROM url u WHERE u.created_at < NOW() - INTERVAL '" + interval + "' RETURNING u.hash";
        List<String> oldHashes = entityManager.createNativeQuery(sql).getResultList();
        hashRepository.save(oldHashes);
        log.info("Cleanup of the old hashes has been completed");
    }
}
