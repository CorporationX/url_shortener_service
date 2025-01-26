package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.service.hash.HashService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UrlCleanerScheduler {

    private final UrlService urlService;
    private final HashService hashService;

    @Value("${schedule.url-cleaner-scheduler.batch-size}")
    private int batchSize;

    @Transactional
    @Scheduled(cron = "${schedule.url-cleaner-scheduler.cron}")
    public void cleanUrls() {
        List<Hash> hashes =  urlService.deleteExpiredUrls(batchSize)
                .stream()
                .map(url -> new Hash(url.getHash()))
                .toList();

        hashService.saveAll(hashes);
    }
}
