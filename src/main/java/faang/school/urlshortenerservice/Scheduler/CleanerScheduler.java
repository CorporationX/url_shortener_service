package faang.school.urlshortenerservice.Scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${schedule.cron}")
    @Transactional
    public void cleanUrls() {
        List<Hash> oldHashes = urlRepository
                .deleteOlderThanYear()
                .stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(oldHashes);
    }
}
