package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.model.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final UrlCleanerAsync urlCleanerAsync;

    @Scheduled(cron = "${url.cleaner.cron}")
    @Transactional
    public void removeOldUrls() {
        List<Url> urls = urlRepository.deleteOldUrls();
        if (!urls.isEmpty()) {
            List<Hash> hashes = urls.stream()
                    .map(url -> new Hash(url.getHash()))
                    .toList();
            ListUtils.partition(hashes, 500).forEach(urlCleanerAsync::cleanUrl);
            log.info("Url cleaner was triggerred. Removed {} urls", urls.size());
        }
    }
}
