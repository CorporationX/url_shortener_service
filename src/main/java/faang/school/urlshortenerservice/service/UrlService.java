package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exeption.UrlNotFoundException;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Transactional
    public String createShortUrl(String longUrl) {
        validateUrl(longUrl);

        String hash = hashGenerator.generateHash();

        Url url = Url.builder()
                .hash(hash)
                .url(longUrl)
                .build();
        urlRepository.save(url);

        Hash hashEntity = Hash.builder()
                .hash(hash)
                .url(url)
                .build();
        hashRepository.save(hashEntity);

        urlCacheRepository.save(hash, longUrl);
        cacheLongUrl(hash, longUrl);
        return "http://short.url/" + hash;
    }

    @Cacheable(cacheNames = "shortenedUrls", key = "#hash")
    public String getLongUrl(String hash) {
        Url url = urlRepository.findById(hash)
                .orElseThrow(() -> new UrlNotFoundException("No URL found for hash: " + hash));
        return url.getUrl();
    }

    private void validateUrl(String url) {
        if (!url.matches("(https?://)?(www.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&//=]*)")) {
            throw new IllegalArgumentException("Invalid URL format.");
        }
    }

    @CachePut(cacheNames = "shortenedUrls", key = "#hash")
    public void cacheLongUrl(String hash, String longUrl) {

    }

    @CacheEvict(cacheNames = "shortenedUrls", key = "#hash")
    public void removeCachedUrl(String hash) {

    }
}