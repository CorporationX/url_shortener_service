package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.exception.RemoveLinksAndReturnHashException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;

    @Scheduled(cron = "${url.cleaner.cron}")
    @Transactional
    public void removeOldLinksAndReturnHash() {
        try {
            List<String> hashes = urlRepository.removeOldLinksAndReturnHash();
            if (hashes == null || hashes.isEmpty()) {
                log.info("No old links found or null returned, nothing to save to hash repository");
                return;
            }
            hashRepository.save(hashes);
            log.info("Saved {} hashes to hash repository", hashes.size());
        } catch (DataAccessException e) {
            log.error("Database error while removing old links or saving hashes", e);
            throw new RemoveLinksAndReturnHashException("Failed to clean URLs due to database error", e);
        } catch (TransactionException e) {
            log.error("Transaction error while processing URL cleanup", e);
            throw new RemoveLinksAndReturnHashException("Failed to clean URLs due to transaction issue", e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid arguments provided for URL cleanup", e);
            throw new RemoveLinksAndReturnHashException("Invalid configuration for URL cleanup", e);
        } catch (NullPointerException e) {
            log.error("Unexpected null value encountered during URL cleanup", e);
            throw new RemoveLinksAndReturnHashException("Unexpected null value in URL cleanup", e);
        }
    }
}
