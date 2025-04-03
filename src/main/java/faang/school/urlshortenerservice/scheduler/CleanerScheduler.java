package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    @Value("${clean-scheduler.date.interval}")
    private String interval;

    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;

    @Scheduled(cron = "${clean-scheduler.cron}")
    @Transactional
    public void deleteUnusedUrls() {
        log.info("Запуск запланированного задания по удалению устаревших ссылок");
        List<Hash> hashes = urlRepository.deleteOutdatedUrlsAndReturnHashes(interval);
        hashRepository.saveAll(hashes);
        log.info("Окончание запланированного задания по удалению устаревших ссылок");
    }
}
