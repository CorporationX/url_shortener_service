package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UrlCleanerService {

    private final UrlRepository urlRepository;
    private final RedisHashPoolService hashPool;

    @Value("${app.scheduler.url_cleaner.url_lifetime_days}")
    private int urlLifetimeDays;

    @Transactional
    public void removeExpiredUrlsAndResaveHashes() {
        LocalDateTime expirationDate = LocalDateTime.now().minusDays(urlLifetimeDays);
        List<String> expiredHashes = urlRepository.deleteOldUrlsAndReturnHashes(expirationDate);
        hashPool.returnHashes(expiredHashes);
    }
}