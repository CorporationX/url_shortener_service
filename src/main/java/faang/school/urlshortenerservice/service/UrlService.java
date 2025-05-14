package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.HashNotFoundException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private static final String SHORT_URL_PATTERN = "https://shorter-x/";

    private final UrlRepository urlRepository;
    private final HashGeneratorService hashGeneratorService;
    private final HashCacheService hashCacheService;
    private final UrlCacheRepository urlCacheRepository;

    @Value("${url-manipulation-setting.months-to-clear-url}")
    private int monthsToClearUrl;

    @Transactional
    public String createShortUrl(UrlDto urlDto) {
        String url = urlDto.longUrl();
        String hash = hashCacheService.getHash();
        if (hash == null) {
            throw new HashNotFoundException("Cache hash is empty");
        }
        Url entityUrl = urlRepository.save(createUrlAssociation(url, hash));
        log.debug("Created new association on url {} to DB", url);
        try {
            urlCacheRepository.cacheUrl(entityUrl);
            log.debug("Created new association on url {} to Redis", url);
        } catch (DataAccessException e) {
            log.warn("Caching process on redis for url {} failed. Details: {}", entityUrl.getHash(), e.toString());
        }
        return SHORT_URL_PATTERN.concat(hash);
    }

    public String redirectToOriginalUrl(String shortUrl) {
        int lastIndex = shortUrl.lastIndexOf('/');
        String hash = shortUrl.substring(lastIndex, shortUrl.length() - 1);
        Url url = urlCacheRepository.getUrlByHash(hash);
        if (url == null) {
            url = urlRepository.getByHash(hash).orElseThrow(
                    () -> new UrlNotFoundException("Original url with hash %s not found", hash));
        }
        return url.getUrl();
    }

    @Transactional
    public void cleanUnusedAssociations() {
        LocalDateTime createdAt = LocalDateTime.now().minusMonths(monthsToClearUrl);
        List<String> hashes = urlRepository.findAndDeleteByCreatedAtBefore(createdAt);
        hashes.forEach(urlCacheRepository::evictUrlByHash);
        log.debug("Url before {} created date cleared successfully", createdAt);
        hashGeneratorService.processAllHashes(hashes);
        log.debug("Hashes returning on database successfully");
    }

    private Url createUrlAssociation(String url, String hash) {
        return Url.builder()
                .url(url)
                .hash(hash)
                .build();
    }
}
