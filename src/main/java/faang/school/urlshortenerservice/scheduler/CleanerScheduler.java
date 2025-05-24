package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;


    @Scheduled(cron = "${scheduler.cron}")
    @Transactional
    public void cleanerScheduler() {
        log.debug("Cleaner scheduler started");
        List<String> unusedHash = urlRepository.deleteOldUrl();
        if(!unusedHash.isEmpty())
            hashRepository.save(unusedHash);
    }
}
