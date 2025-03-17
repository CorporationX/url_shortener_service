package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    @Value("${clean-expired-url.available-interval:1 year}")
    private String interval;

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Transactional
    @Scheduled(cron = "${clean-expired-url.cron}")
    public void cleanExpiredUrl() {
        List<String> hashes = urlRepository.getHashesWithExpiredDates(interval);
        hashRepository.save(hashes);
    }
}
