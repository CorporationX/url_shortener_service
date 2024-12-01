package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.config.properties.HashProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlServiceImpl urlService;
    private final HashProperties hashProperties;
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${scheduler.old_url_hashes}")
    public void cleanOldUrlHashes() {
        long interval = hashProperties.getIntervalInMillis();
        List<String> deletedHashes = cleanOldUrls();
        log.info("Deleted hashes of old URL: {}", deletedHashes);
    }

    private List<String> cleanOldUrls() {
        List<String> deletedHashes = urlRepository.deleteOldUrlsAndReturnHashes(hashProperties.getInterval());

        for (String hash : deletedHashes) {
            Hash hashEntity = hashRepository.findByHash(hash);
            if (hashEntity != null) {
                hashEntity.setUrl(null);
                hashRepository.save(hashEntity);
            }
        }
        return deletedHashes;
    }
}