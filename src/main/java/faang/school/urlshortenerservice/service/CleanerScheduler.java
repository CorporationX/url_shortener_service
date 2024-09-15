package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    @Value("${url.days-to-live}")
    private int daysToLive;

    @Scheduled(cron = "${cleaner-scheduler.cron}")
    @Transactional
    public void cleanOldUrls() {
        List<Url> urls = urlRepository.findUrlsOlderThan(LocalDateTime.now().minusDays(daysToLive));
        List<Hash> hashes = urls.stream().map(Url::getHash).map(Hash::new).toList();
        hashRepository.saveAll(hashes);
    }
}
