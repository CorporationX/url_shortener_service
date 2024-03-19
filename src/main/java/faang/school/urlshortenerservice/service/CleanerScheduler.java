package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Async("taskExecutor")
    @Scheduled(cron = "${cleaner_scheduler.cron}")
    @Transactional
    public void cleanUrls() {
        List<Hash> oldHashes = urlRepository.deleteOlderThanOneYearUrl()
                .stream().map(Hash::new).toList();
        hashRepository.saveAll(oldHashes);
    }
}
