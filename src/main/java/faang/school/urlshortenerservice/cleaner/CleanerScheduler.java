package faang.school.urlshortenerservice.cleaner;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;

    @Scheduled(cron = "${url.cleaner.cron}")
    @Transactional
    public void removeOldUrls() {
        List<String> freedHashes = urlRepository.deleteOldEntriesAndReturnHashes();
        if (!freedHashes.isEmpty()) {
            hashRepository.save(freedHashes);
        } else {
            log.info("No hashes to transfer");
        }
    }
}
