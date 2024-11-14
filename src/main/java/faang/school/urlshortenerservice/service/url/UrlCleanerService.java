package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.repository.cache.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import faang.school.urlshortenerservice.service.hash.HashService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UrlCleanerService {
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashService hashService;

    @Value("${app.scheduler.url_cleaner.url_lifetime_days}")
    private int days;

    @Transactional
    public void removeExpiredUrls() {
        List<String> hashes = urlRepository.getAndDeleteUrlsByDate(LocalDateTime.now().minusDays(days));
        hashService.saveAllBatch(hashes);
        urlCacheRepository.deleteAll(hashes);
    }
}
