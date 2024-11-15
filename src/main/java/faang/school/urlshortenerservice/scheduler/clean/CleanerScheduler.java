package faang.school.urlshortenerservice.scheduler.clean;


import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Data
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${spring.interval-hours}")
    private Long urlLifeTime;

    @Scheduled(cron = "${spring.scheduler.cron-for-cleaner}")
    @Transactional
    public void cleaner() {
        LocalDateTime dateTime = LocalDateTime.now().minusHours(urlLifeTime);
        hashRepository.saveAll(urlRepository.deleteOldUrlsAndReturnHashesAsHashEntities(dateTime));
    }
}
