package faang.school.urlshortenerservice.sheduler;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.service.HashService;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanScheduler {
    private final UrlService urlService;
    private final HashService hashService;

    @Scheduled(cron = "${cleaner-scheduler.cron}")
    public void cleanOldUrls() {
        List<Url> oldUrls = urlService.findOldUrls();

        List<Hash> hashes = oldUrls.stream()
                .map(Url::getHash)
                .map(Hash::new)
                .toList();

        hashService.saveAllHashes(hashes);
    }
}
