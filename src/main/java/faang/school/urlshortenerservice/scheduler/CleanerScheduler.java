package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.CleanerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final CleanerService cleanerService;


    @Scheduled(cron = "${scheduler.cleaner.cron}")
    public void cleanOldUrls(){
        cleanerService.cleanExpiredUrls();
    }
}
