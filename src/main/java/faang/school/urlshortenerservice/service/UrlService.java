package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.NoAvailableHashException;
import faang.school.urlshortenerservice.exception.SQLSaveException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    @Value("${url.base_url}")
    private String baseUrl;

    @Timed(value = "save_original_url_timer", description = "Time taken to save original URL",
            histogram = true, percentiles = {0.5, 0.95})
    @Transactional
    public String saveOriginalUrl(String url) {
        log.info("Creating short URL for originalUrl={}", url);
        String hash = hashCache.getHash().orElseThrow(() -> {
            log.error("No available hashes for url={}", url);
            return new NoAvailableHashException(url);
        });

        try {
            urlRepository.save(hash, url);
        } catch (DataAccessException exception) {
            log.error("Failed to save to PostgreSQL: url={}", url, exception);
            throw new SQLSaveException(url);
        }

        saveToRedis(hash, url);

        return baseUrl.concat(hash);
    }

    @Timed(value = "get_original_url_service_timer", description = "Time taken to get original URL in service layer",
            histogram = true, percentiles = {0.5, 0.95})
    public String getOriginalUrl(String hash) {
        log.info("Retrieving URL for hash={}", hash);
        return urlCacheRepository.findUrlByHash(hash)
                .orElseGet(() -> {
                    String url = urlRepository.findByHash(hash)
                            .orElseThrow(() -> {
                                log.error("URL not found for hash={}", hash);
                                return new UrlNotFoundException(
                                        String.format("Original URL with hash %s not found", hash)
                                );
                            });
                    saveToRedis(hash, url);
                    return url;
                });
    }

    private void saveToRedis(String hash, String url) {
        try {
            urlCacheRepository.save(hash, url);
        } catch (Exception exception) {
            log.warn("Failed to save to Redis: hash={}, url={}", hash, url, exception);
        }
    }
}
