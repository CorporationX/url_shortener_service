package faang.school.urlshortenerservice.service.cleaner;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${hash.clean.scheduler.cron}")
    @Transactional
    @Async("executor")
    public void cleanDB() {
        log.info("start cleanDB");

        List<String> existingHashes = urlRepository.getHashesAndDeleteOldUrls();
        log.info("get {} existingHashes", existingHashes.size());

        hashRepository.saveAllHashesBatched(existingHashes.stream()
                .map(Hash::new)
                .toList());

        log.info("finish cleanDB");
    }
}
