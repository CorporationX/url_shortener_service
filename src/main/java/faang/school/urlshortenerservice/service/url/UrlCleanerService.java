package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.repository.cache.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import faang.school.urlshortenerservice.service.hash.HashService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlCleanerService {
    private final UrlRepository urlRepository;
    private final HashService hashService;
    private final UrlCacheRepository urlCacheRepository;

    @Value("${app.scheduler.url_cleaner.url_lifetime_days:365}")
    private int days;

    @Transactional
    public void removeExpiredUrlsAndResaveHashes() {
        List<String> freedHashes = urlRepository.getAndDeleteUrlsByDate(LocalDateTime.now().minusDays(days));
        hashService.saveAllBatch(freedHashes);
        urlCacheRepository.deleteAllByHashes(freedHashes);
    }
}