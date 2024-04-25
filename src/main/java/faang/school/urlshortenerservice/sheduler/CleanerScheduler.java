package faang.school.urlshortenerservice.sheduler;

import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    @Value("${scheduler.expired-time-years}")
    private Long expiredTimeInYears;

    @Scheduled(cron = "${scheduler.cron.cleaner}")
    @Transactional
    @Async
    public void cleanOldUrls() {
        Set<String> hashes = urlRepository.deleteByDateAndGetHashes(LocalDateTime.now().minusYears(expiredTimeInYears));
        hashRepository.save(hashes);
    }

}
