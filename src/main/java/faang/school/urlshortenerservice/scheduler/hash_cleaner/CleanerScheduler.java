package faang.school.urlshortenerservice.scheduler.hash_cleaner;

import faang.school.urlshortenerservice.repository.hash.impl.HashRepositoryImpl;
import faang.school.urlshortenerservice.repository.url.impl.UrlRepositoryImpl;
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

    private final HashRepositoryImpl hashRepository;
    private final UrlRepositoryImpl urlRepository;

    @Transactional
    @Scheduled(cron = "${scheduler.cron.clean-hashes}")
    public void cleanUnusedHashes() {
        log.info("Start cleaning unused hashes");
        List<String> hashes = urlRepository.retrieveAllUrlsElderOneYear();
        log.info("{} hashes were retrieved from URL repository}",hashes.size());
        hashRepository.saveHashes(hashes);
        log.info("{} free hashes were saved to hash repository}",hashes.size());
    }
}
