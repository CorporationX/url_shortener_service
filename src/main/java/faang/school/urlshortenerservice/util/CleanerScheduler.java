package faang.school.urlshortenerservice.util;

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

    @Value("${hash-cleaner.retention-period.years}")
    private int retentionPeriodYears;
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Transactional
    @Scheduled(cron = "${hash-cleaner.cron}")
    public void cleanOldUrls() {
        log.info("Starting a cleanup of the old urls");
        List<Url> urls = urlRepository.findAllByCreatedAtBefore(LocalDateTime.now().minusYears(retentionPeriodYears));
        if (urls.isEmpty()) {
            return;
        }
        List<String> hashes = urls.stream()
                .map(Url::getHash)
                .toList();
        hashRepository.save(hashes);
        urlRepository.deleteAllInBatch(urls);
        log.info("The old urls are cleaned");
    }
}
