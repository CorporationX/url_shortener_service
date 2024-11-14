package faang.school.urlshortenerservice.scheduler.cleanHash;


import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${spring.scheduler.cron-for-cleaner}")
    @Transactional
    public void cleaner() {
        hashRepository.saveAll(urlRepository.deleteOldUrlsAndReturnHashesAsHashEntities());
    }
}
