package faang.school.urlshortenerservice.schedule;

import faang.school.urlshortenerservice.repository.HashRepositoryJdbc;
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

    private final UrlRepository urlRepository;
    private final HashRepositoryJdbc hashRepositoryJdbc;

    @Transactional
    @Scheduled(cron = "${url.cleaner.cron}")
    public void cleanUnusedHashes() {
        log.info("Clean unused hashes");
        List<String> hashes = urlRepository.findAndDeleteOldUrl();
        hashRepositoryJdbc.save(hashes);
    }
}
