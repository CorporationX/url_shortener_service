package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${app.cleaner.cron}")
    @Transactional
    public void cleanOldHashes() {
        List<String> hashes = urlRepository.deleteOldUrls();
        if (!hashes.isEmpty()) {
            hashRepository.saveHashes(hashes);
            log.debug("Удалено {} Url адресов", hashes.size());
        } else {
            log.debug("Url адресов подлежащих к удалению не найдено");
        }

    }
}
