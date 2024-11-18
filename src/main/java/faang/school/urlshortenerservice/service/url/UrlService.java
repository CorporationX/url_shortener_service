package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.exception.UrlExpiredException;
import faang.school.urlshortenerservice.model.url.Url;
import faang.school.urlshortenerservice.repository.postgres.url.UrlRepository;
import faang.school.urlshortenerservice.repository.redis.UrlCacheRepository;
import faang.school.urlshortenerservice.util.HashCache;
import faang.school.urlshortenerservice.validator.AppUrlValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UrlService {
    @Value("${app.get-hash-url}")
    private String getHashUrl;

    private final AppUrlValidator appUrlValidator;
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    public String generateShortUrl(String longUrl) {
        appUrlValidator.validate(longUrl);

        String hash = hashCache.getHash();

        urlRepository.save(build(hash, longUrl));
        urlCacheRepository.save(hash, longUrl);

        return buildRedirectUrl(hash);
    }

    public String getUrlByHash(String hash) {
        String url = urlCacheRepository.get(hash);
        if (url == null) {
            url = urlRepository.findByHash(hash)
                    .map(Url::getUrl)
                    .orElseThrow(() -> new UrlExpiredException(buildRedirectUrl(hash)));
        }
        return url;
    }

    @Transactional
    public List<String> cleanHashes() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        return urlRepository.getOldUrlsAndDelete(oneYearAgo);
    }

    private String buildRedirectUrl(String hash) {
        return getHashUrl + hash;
    }

    private Url build(String hash, String url) {
        return Url.builder()
                .hash(hash)
                .url(url)
                .build();
    }
}
