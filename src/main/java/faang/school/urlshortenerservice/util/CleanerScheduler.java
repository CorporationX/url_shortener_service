package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Setter
public class CleanerScheduler {
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Value("${schedulers.config.numberOfDaysForOutdatedHashes:365}")
    private int numberOfDaysForOutdatedHashes;

    @Scheduled(cron = "${schedulers.config.cleanupOutdatedHashes.cronExpression}")
    @Transactional
    public void cleanupOutdatedHashes () {
        List<String> retrievedHashes = urlRepository.deleteOldUrlsAndReturnHashes(numberOfDaysForOutdatedHashes);
        if (retrievedHashes.isEmpty()) {
            log.info("no outdated short links found");
            return;
        }

        log.info("{} of outdated short links found and removed.", retrievedHashes.size());
        hashRepository.save(retrievedHashes);
        retrievedHashes.forEach(urlCacheRepository::deleteByHash);
    }
}
