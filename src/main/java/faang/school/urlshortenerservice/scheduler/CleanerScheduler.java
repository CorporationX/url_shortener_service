package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${cleaner.scheduler.cron}")
    public void cleanOldHashes() {
        log.info("Starting scheduled cleanup of old URLs and hashes");

        log.debug("Retrieving expired URLs");
        List<String> expiredUrlHashes = urlRepository.retrieveExpiredHashes();
        log.info("Deleted {} expired URLs", expiredUrlHashes.size());

        int amountToDelete = expiredUrlHashes.size();
        log.debug("Deleting {} old hashes", amountToDelete);
        List<Hash> deletedHashes = hashRepository.findAndDelete(amountToDelete);
        log.info("Successfully deleted {} hashes", deletedHashes.size());

        log.info("Cleanup completed successfully");
    }
}
