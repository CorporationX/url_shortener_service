package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashesRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlsRepository urlsRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashesRepository hashesRepository;

    @Value("${url-shortener-service.cleaner.expired-period-seconds}")
    private long expiredPeriod;

    @Scheduled(cron = "${url-shortener-service.cleaner.cron}")
    @Transactional
    public void removeExpired() {
        log.info("Remove expired scheduler");
        List<String> deletedHashes = urlsRepository.deleteExpired(expiredPeriod);
        deletedHashes.forEach(urlCacheRepository::delete);
        hashesRepository.save(deletedHashes.toArray(new String[0]));
    }

}
