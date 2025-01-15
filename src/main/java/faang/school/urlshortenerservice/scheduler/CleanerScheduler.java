package faang.school.urlshortenerservice.scheduler;

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

    @Value("${hash.cleaner.times:1}")
    private long timesAgo;

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Transactional
    @Scheduled(cron = "${hash.cleaner.cron}")
    public void clean() {
        LocalDateTime timeAgo = LocalDateTime.now().minusYears(timesAgo);
        List<Url> urls = urlRepository.deleteByCreatedAtAfter(timeAgo);
        if (!urls.isEmpty()) {
            List<String> hashes = urls.stream().map(Url::getHash).toList();
            hashRepository.saveAll(hashes);
        }
    }
}