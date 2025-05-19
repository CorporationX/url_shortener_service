package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.config.properties.CleanSchedulerProperties;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CleanScheduler {

    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;
    private final CleanSchedulerProperties properties;

    @Scheduled(cron = "#{@cleanSchedulerProperties.cron}")
    @Transactional
    public void deleteUnusedUrls() {
        List<Hash> hashes = urlRepository.deleteOldUrlsAndReturnHashes(properties.getExpirationDays());
        if (!hashes.isEmpty()) {
            hashRepository.saveAll(hashes);
            log.info("Restored {} hashes to the pool", hashes.size());
        }
    }
}
