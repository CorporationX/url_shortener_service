package faang.school.urlshortenerservice.scheduler.hash_cleaner;

import faang.school.urlshortenerservice.repository.hash.impl.HashRepositoryImpl;
import faang.school.urlshortenerservice.repository.url.impl.UrlRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final HashRepositoryImpl hashRepository;
    private final UrlRepositoryImpl urlRepository;

    @Transactional
    @Scheduled(cron = "${scheduler.cron.clean-hashes}")
    public void cleanUnusedHashes() {
        List<String> hashes = urlRepository.retrieveAllUrlsElderOneYear();
        hashRepository.saveHashes(hashes);
    }
}
