package urlshortenerservice.scheduler;

import urlshortenerservice.entity.Hash;
import urlshortenerservice.entity.Url;
import urlshortenerservice.repository.HashRepository;
import urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${shortener.db.url-ttl}")
    private int urlTtl;

    @Transactional
    @Scheduled(cron = "${scheduler.cron.clean}")
    public void clean() {
        LocalDateTime daysAgo = LocalDateTime.now().minusDays(urlTtl);
        List<Url> urls = urlRepository.findByCreatedAtBefore(daysAgo);

        if (!urls.isEmpty()) {
            List<Hash> hashes = urlRepository.deleteByHashAndReturn(urls.stream()
                            .map(Url::getHash)
                            .toList()).stream()
                    .map(hash -> Hash.builder().hash(hash).build())
                    .toList();

            hashRepository.saveAll(hashes);
        }
    }
}
