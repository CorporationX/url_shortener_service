package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CleanerScheduler — джоба для очистки устаревших коротких ссылок и возвращение hash
 * для переиспользования
 *
 * @author bozya
 * @since 23.09.2025
 */
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;

    @Value("${app.cleanup.retention-period}")
    private String retentionPeriod;

    @Transactional
    @Scheduled(cron = "${app.cleanup.cron}")
    public void cleanOldUrls() {
        List<String> hashes = urlRepository.deleteOldUrlsAndReturnHash(retentionPeriod);

        if(!hashes.isEmpty()) {
            hashRepository.saveAll(hashes);
        }
    }
}