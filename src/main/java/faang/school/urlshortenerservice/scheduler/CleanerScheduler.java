package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${cleanup.expiration-years:1}")
    private int yearsBeforeExpiration;

    @Transactional
    @Scheduled(cron = "${cleanup.cron:0 0 0 * * *}")
    public void cleanOldUrls() {
        LocalDateTime expirationDate = LocalDateTime.now().minusYears(yearsBeforeExpiration);
        List<String> hashes = new ArrayList<>(urlRepository.deleteOldUrlsAndReturnHashes(expirationDate));
        hashRepository.save(hashes);
    }
}
