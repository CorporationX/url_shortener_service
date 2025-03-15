package faang.school.urlshortenerservice.sheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${url.schedule.cleaner}")
    @Transactional
    public void cleanExpiredUrls() {
        List<Hash> freeHashes = urlRepository.removeExpiredUrls().stream()
                .map(Hash::new)
                .toList();

        hashRepository.saveAll(freeHashes);
    }
}
