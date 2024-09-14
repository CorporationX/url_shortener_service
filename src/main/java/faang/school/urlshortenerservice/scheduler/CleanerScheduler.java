package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepositoryImpl;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepositoryImpl hashRepositoryImpl;

    @Transactional
    @Scheduled(cron = "${scheduler.cron.url_removal}")
    public void removeExpiredUrls() {

        LocalDateTime comparingTime = LocalDateTime.now().minusYears(1L);
        List<Url> urlsForRemove = urlRepository.getAllBefore(comparingTime);
        List<String> freeHashes = urlsForRemove.stream().map(Url::getHash).toList();
        hashRepositoryImpl.saveHashes(freeHashes);
    }
}
