package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private final HashRepository hashRepository;

    @Value("${url-shortener.scheduler.moths-to-clean}")
    private int monthsToClean;

    @Transactional
    @Scheduled(cron = "${url-shortener.scheduler.cleaner-cron}")
    public void clean() {
        log.info("Cleaning up expired URLs...");

        LocalDateTime expirationDate = LocalDateTime.now().minusMonths(monthsToClean);
        List<String> deletedUrls = urlRepository.deleteByCreatedAtBefore(expirationDate);
        log.info("Deleted {} expired URLs", deletedUrls.size());
        if (!deletedUrls.isEmpty()) {
            List<Hash> hashes = deletedUrls.stream().map(hash -> Hash.builder().hash(hash).build()).toList();
            hashRepository.saveAll(hashes);
            log.info("Deleted URLs and saved in hash: {}", deletedUrls);
        }

        log.info("Cleaning up expired URLs completed");
    }
}
