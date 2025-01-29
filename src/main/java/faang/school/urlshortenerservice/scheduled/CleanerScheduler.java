package faang.school.urlshortenerservice.scheduled;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanOldUrlsAndSaveHashes() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);

        List<String> hashes = urlRepository.deleteOldUrlsAndReturnHashes(oneYearAgo);

        if (!hashes.isEmpty()) {
            hashRepository.save(hashes);
        }
    }
}