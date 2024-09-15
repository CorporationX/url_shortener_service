package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.HashService;
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
    private final HashService hashService;

    @Transactional
    @Scheduled(cron = "${scheduler.cron.url_removal}")
    public void removeExpiredUrls() {

        LocalDateTime comparingTime = LocalDateTime.now().minusYears(1L);
        List<Url> urlsForRemove = urlRepository.findAllByCreatedAtBefore(comparingTime);
        List<String> freeHashes = urlsForRemove.stream().map(Url::getHash).toList();
        hashService.saveAllHashes(freeHashes);
    }
}
