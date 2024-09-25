package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class HashService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;

    @Value("${hash.count_hash}")
    private int countHash;
    @Value("${hash.unique_numbers}")
    private int uniqueNumbers;

    @Transactional
    public String getShortUrl(String url) {
        String hash = hashCache.getHashFromUser();
        urlRepository.save(Url.builder()
                .hash(hash)
                .url(url)
                .build());
        urlCacheRepository.saveHashAndUrl(hash, url);

        return createShortUrl(url, hash);
    }

    @Transactional(readOnly = true)
    public String getOriginalUrl(String hash) {
        return urlCacheRepository.getOriginalUrl(hash)
                .or(() -> urlRepository.findUrlByHash(hash))
                .orElseThrow(() -> new UrlNotFoundException("Url not found for this hash"));
    }

    private String createShortUrl(String url, String hash) {
        String[] parts = url.split("/");
        return String.join("/", parts[0], parts[1], parts[2]) + "/" + hash;
    }
}
