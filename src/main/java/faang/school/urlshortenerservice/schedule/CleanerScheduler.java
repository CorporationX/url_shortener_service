package faang.school.urlshortenerservice.schedule;

import faang.school.urlshortenerservice.repository.jpa.HashRepository;
import faang.school.urlshortenerservice.service.UrlService;
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

    private final UrlService urlService;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${url.schedule.cron}")
    @Transactional
    public void releaseHashes() {
        List<String> hashes = urlService.deleteUnusedHashes();
        hashRepository.saveBatch(hashes);
        log.info("{} hashes have been released and saved", hashes.size());
    }
}
