package faang.school.urlshortenerservice.cleaner.url;

import faang.school.urlshortenerservice.config.properties.url.UrlProperties;
import faang.school.urlshortenerservice.model.hash.Hash;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final UrlProperties urlProperties;

    @Scheduled(cron = "${hash.cleaner.cron}")
    @Transactional
    public void clean() {
        List<String> hashesStrings = urlRepository
                .deleteExpiredUrlsAndReturnHashes(LocalDateTime.now()
                        .minusYears(urlProperties.getTimeLimit().getYear()));
        List<Hash> hashes = hashesStrings.stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAllBatched(hashes);
    }
}
