package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    @Value("${schedulers.clear_old_hash_time}")
    private int clearOldHashTime;

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Transactional
    @Scheduled(cron = "${schedulers.hash_cleaner_scheduler}", zone = "${schedulers.hash_cleaner_zone}")
    public void clearOldHash() {
        List<String> hashes = urlRepository.deleteRecordsAndReturnHash(LocalDateTime.now().minusDays(clearOldHashTime));
        log.info("Outdated hashes have been received");
        hashRepository.save(hashes);
        log.info("Outdated hashes are saved to the appropriate table");
    }
}
