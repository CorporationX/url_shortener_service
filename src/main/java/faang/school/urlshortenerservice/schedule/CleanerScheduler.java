package faang.school.urlshortenerservice.schedule;


import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashService hashService;

    /**
     * Удаление ссылок у которых истек TTL с последующим освобождением хэшей
     */
    @Transactional
    @Scheduled(cron = "${hash-generator.cron-delete}")
    public void deleteOldUrl() {
        log.debug("Start deleting old urls");
        List<String> deletedUrls = urlRepository.deleteExpiredUrls();
        deletedUrls.forEach(urlCacheRepository::removeUrl);
        hashService.saveFreeHashes(deletedUrls);
        log.debug("Finished deleting old urls");
    }
}
