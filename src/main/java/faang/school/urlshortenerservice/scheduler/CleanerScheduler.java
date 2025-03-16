package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${cleaner.scheduler.cron}")
    private String cronExpression;

    @Scheduled(cron = "#{@cleanerScheduler.cronExpression}")
    @Transactional
    public void cleanOldUrls() {
        log.info("Starting cleanup process for old URLs");

        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        List<Url> oldUrls = urlRepository.findUrlsOlderThan(oneYearAgo);

        if (oldUrls.isEmpty()) {
            log.info("No old URLs to clean");
            return;
        }

        List<String> hashes = oldUrls.stream()
                .map(Url::getHash)
                .toList();

        hashes.forEach(hash -> hashRepository.save(new Hash(hash)));
        urlRepository.deleteAll(oldUrls);

        log.info("Cleanup completed, deleted {} old URLs and saved {} hashes", oldUrls.size(), hashes.size());
    }
}

