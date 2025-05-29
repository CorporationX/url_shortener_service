package faang.school.urlshortenerservice.service.urlcleanup;

import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.hash.HashService;
import faang.school.urlshortenerservice.util.RetentionPeriodParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "app.service.url-cleanup",
        havingValue = "default",
        matchIfMissing = true
)
public class UrlCleanupServiceImpl implements UrlCleanupService {

    private final HashService hashService;
    private final UrlRepository urlRepository;
    private final RetentionPeriodParser periodParser;

    @Value("${url.cleanup.retention-period}")
    private String urlRetentionPeriod;

    @Override
    @Transactional
    public void cleanExpiredUrls() {
        log.info("Starting cleanup expired URLs...");
        LocalDateTime expiryDate = periodParser.calculateExpiryDate(urlRetentionPeriod);
        List<String> freeHashes = urlRepository.cleanExpiredUrls(expiryDate);
        if (freeHashes.isEmpty()) {
            log.info("There is no URLs older than {}", expiryDate);
            return;
        }
        hashService.save(freeHashes);
        log.info("Expired URLs have been successfully deleted. {} hashes returned in the hash pool", freeHashes.size());
    }
}
