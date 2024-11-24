package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
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
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    @Value("${scheduler.interval.url}")
    private String expirationDate;

    @Scheduled(cron = "${scheduler.cron.clean-url}")
    @Transactional
    public List<Hash> deleteExpiredUrls () {
        return hashRepository.saveAll(urlRepository.getAndDeleteExpiredUrl(expirationDate)
                .stream()
                .map(url -> new Hash(url.getHash()))
                .toList());
    }
}
