package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;

    @Scheduled(cron = "${url.cleaner.cron}")
    @Transactional
    public void removeOldLinks() {
        List<String> hashes = urlRepository.removeOldLinks();
        hashRepository.save(hashes);
    }
}
