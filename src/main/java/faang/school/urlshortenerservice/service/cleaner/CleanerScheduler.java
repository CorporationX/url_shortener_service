package faang.school.urlshortenerservice.service.cleaner;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repositoy.HashRepository;
import faang.school.urlshortenerservice.repositoy.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;

    private final HashRepository hashRepository;

    @Scheduled(cron = "${scheduler.cron}")
    @Transactional
    public void cleanOldUrls() {
        hashRepository.save(urlRepository.deleteOldUrlsAndReturnHashes().stream()
                .map(s -> {
                    Hash hash = new Hash();
                    hash.setHash(s);
                    return hash;
                }).toList());
    }
}
