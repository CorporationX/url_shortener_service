package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.redis.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
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

    private final UrlService urlService;

    @Scheduled(cron = "${hash.cleaner.cron}")
    public void cleanUpOldUrls() {
        urlService.cleanUpOldUrls();
    }
}

