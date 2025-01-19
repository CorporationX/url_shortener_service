package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.redis.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Configuration
@EnableScheduling
@Data
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${hash.cleaner.cron}")
    @Transactional
    public void cleanUpOldUrls() {
        List<String> oldHashes = urlRepository.deleteOldUrlsAndReturnHashes();
        hashRepository.saveBatch(oldHashes);
        urlCacheRepository.deleteBatch(oldHashes);
    }
}

