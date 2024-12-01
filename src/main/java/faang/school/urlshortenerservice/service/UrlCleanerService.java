package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.now;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlCleanerService {
    private final UrlCacheService urlCacheService;
    private final UrlRepository urlRepository;
    private final HashService hashService;

    @Value("${url.days-to-live}")
    private int daysToLive;

    @Transactional
    public void deleteOlderUrlsByTtL() {
        LocalDateTime getUrlTtL = now().minusDays(daysToLive);
        log.info("Starting cleanup for URLs older than {}", getUrlTtL);

        List<Url> oldUrls = urlRepository.getAndDeleteOldUrls(getUrlTtL);
        log.info("Deleted {} old URLs from database", oldUrls.size());

        List<Hash> hashes = oldUrls.stream()
                .map(Url::getHash)
                .map(Hash::new)
                .toList();

        urlCacheService.deleteAllHashes(hashes);
        hashService.saveAllHashes(hashes);
    }
}
