package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.config.ConstantsProperties;
import faang.school.urlshortenerservice.repository.HashRepositoryJdbcImpl;
import faang.school.urlshortenerservice.repository.UrlRepositoryJdbcImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.AopContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final HashRepositoryJdbcImpl hashRepository;
    private final UrlRepositoryJdbcImpl urlRepository;
    private final ConstantsProperties constantsProperties;

    @Scheduled(cron = "${spring.scheduling.expired_hash_cleanup}")
    @Async("taskExecutor")
    public void cleanUpHashes() {
        int cleaned;
        do {
            cleaned = getSelf().cleanUpBatch();
        } while (cleaned == constantsProperties.getCleanUpBatchSize());
    }

    @Transactional
    public int cleanUpBatch() {
        List<String> removedHashes = urlRepository.getHashesAndDelete(
                constantsProperties.getExpirationInterval(),
                constantsProperties.getCleanUpBatchSize()
        );
        hashRepository.save(removedHashes);
        return removedHashes.size();
    }

    private CleanerScheduler getSelf() {
        return (CleanerScheduler) AopContext.currentProxy();
    }
}
