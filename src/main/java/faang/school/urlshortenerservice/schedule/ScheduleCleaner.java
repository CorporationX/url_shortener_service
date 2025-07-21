package faang.school.urlshortenerservice.schedule;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.JdbcUrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduleCleaner {
    private static final int CLEAN_THRESHOLD_YEARS = 1;
    private final JdbcUrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Transactional
    @Scheduled(cron = "${cleaner.schedule-cron}")
    public void cleanStateUrl() {
        LocalDateTime threshold = LocalDateTime.now().minusYears(CLEAN_THRESHOLD_YEARS);

        List<String> freedHashes = urlRepository.deleteOldAndReturnHashes(threshold);
        if (freedHashes.isEmpty()) {
            log.debug("No hashes were freed");
        }

        hashRepository.saveHashes(freedHashes);
        log.info("Freed {} hashes", freedHashes.size());
    }
}
