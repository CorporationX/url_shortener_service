package faang.school.url_shortener_service.scheduler;

import faang.school.url_shortener_service.entity.Hash;
import faang.school.url_shortener_service.entity.Url;
import faang.school.url_shortener_service.repository.url.UrlRepository;
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
public class UrlCleanerScheduler {
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
            log.info("Url cleaner was triggered. Removed {} urls", urls.size());
        }
    }
}