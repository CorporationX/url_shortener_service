package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.cache.hash.HashCache;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final UrlCacheService urlCacheService;
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${url.validity-period.hours:5}")
    private int validityPeriodHours;

    @Override
    @Async("hashCacheExecutor")
    public CompletableFuture<ShortUrlDto> createShortUrl(String longUrl, HttpServletRequest request) {
        return hashCache.getHashAsync().thenCompose(hash ->
                CompletableFuture.supplyAsync(() -> {
                    saveUrlTransactional(hash, longUrl);
                    String baseUrl = getBaseUrl(request);
                    String shortUrl = baseUrl + "/url/redirect/" + hash;
                    log.info("Short URL created asynchronously: {} -> {}", hash, longUrl);
                    return new ShortUrlDto(shortUrl);
                })
        );
    }

    @Transactional
    public void saveUrlTransactional(String hash, String longUrl) {
        Url url = Url.builder()
                .hash(hash)
                .url(longUrl).build();
        urlRepository.save(url);
        urlCacheService.saveNewPair(hash, longUrl);
    }

    private String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        return scheme + "://" + serverName + ":" + serverPort + contextPath + servletPath;
    }

    @Override
    public String getLongUrl(String hash) {
        String url = urlCacheService.getByHash(hash);
        if (url != null) {
            return url;
        }

        Optional<Url> urlOptional = urlRepository.findByHash(hash);
        if (urlOptional.isPresent()) {
            url = urlOptional.get().getUrl();
            return url;
        }

        throw new EntityNotFoundException("URL not found for hash: " + hash);
    }

    @Override
    @Transactional
    public void deleteOldUrls(){
        LocalDateTime validityPeriodStart = LocalDateTime.now(ZoneOffset.UTC).minusHours(validityPeriodHours);
        List<String> deletedHashes = urlRepository.deleteOlderThan(validityPeriodStart);
        log.info("Removed {} lines from urls", deletedHashes.size());
        hashRepository.saveAll(deletedHashes);
        log.info("Added {} lines to hash table", deletedHashes.size());
        deletedHashes.forEach(urlCacheService::deletePairByHash);
    }
}
