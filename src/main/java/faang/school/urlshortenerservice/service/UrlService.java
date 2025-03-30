package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.UrlBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlBuilder urlBuilder;

    @Value("${url.expired-time-in-month}")
    private long expiredPeriod;

    @Transactional
    public URL getShortUrl(String originalUrl) {
        String hash = hashCache.getHash();

        Url newUlr = Url.builder()
                .url(originalUrl)
                .hash(hash)
                .expiredAt(LocalDateTime.now().plusMonths(expiredPeriod))
                .build();

        urlRepository.save(newUlr);
        urlCacheRepository.save(hash, originalUrl);

        return urlBuilder.createShortUrl(hash);
    }

    @Transactional(readOnly = true)
    public String getOriginalUrl(String hash) {
        String originalUrl = urlCacheRepository.getUrl(hash);

        if (originalUrl != null) {
            return originalUrl;
        }

        return urlRepository.findByHash(hash)
                .orElseThrow(() -> {
                    hashRepository.save(new Hash(hash));
                    return new NoSuchElementException(String.format("Hash #%s not found or deleted", hash));
                });
    }

    @Transactional
    public void removeExpiredUrls() {
        List<String> hashes = urlRepository.deleteExpiredUrlsAndReturnHashes();
        hashRepository.saveHashes(hashes.toArray(new String[0]));
    }
}
