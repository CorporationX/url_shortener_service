package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.SaveDbJdbc;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableScheduling
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final SaveDbJdbc saveDbJdbc;

    @Scheduled(cron = "#{@environment.getProperty('cron.url_cleaner')}")
    @Transactional
    public void removeOldUrl() {
        log.info("Removing old urls");
        List<String> freeHashes = urlRepository.removeOldUrls();
        saveDbJdbc.save(freeHashes);
    }
}
