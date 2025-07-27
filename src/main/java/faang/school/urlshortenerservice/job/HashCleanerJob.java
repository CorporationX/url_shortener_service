package faang.school.urlshortenerservice.job;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.service.HashService;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class HashCleanerJob {

    private final UrlService urlService;
    private final HashService hashService;

    @Scheduled(cron = "${app.job.hash-cleaner.cron}")
    public void cleanExpiredUrlsJob() {
        log.info("Cleaning expired urls from hash repository");

        List<Url> urlList = urlService.getExpiredUrlLocked();
        log.info("Number of expired urls from hash cleaner: {}", urlList.size());

        List<Hash> hashList = urlList.stream()
                .map((url) -> new Hash(url.getHash()))
                .toList();
        log.info("Number of hashes to return: {}", hashList.size());

        hashService.returnHashesFromExpiredUrls(hashList);

        log.info("Finished cleaning expired urls from hash repository");
    }
}
