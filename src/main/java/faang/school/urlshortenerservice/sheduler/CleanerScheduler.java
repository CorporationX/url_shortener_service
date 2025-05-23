package faang.school.urlshortenerservice.sheduler;

import faang.school.urlshortenerservice.repository.JdbcHashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final JdbcHashRepository jdbcHashRepository;

    @Scheduled(cron = "${scheduler.cleaner.cron}")
    @Transactional
    public void cleanOld() {
        List<String> freed = urlRepository.deleteOldAndReturnHashes();
        if (!freed.isEmpty()) {
            jdbcHashRepository.save(freed);
        }
        log.info("Cleaned and recycled {} old hashes", freed.size());
    }
}