package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.IncorrectUrl;
import faang.school.urlshortenerservice.hash.HashCache;
import faang.school.urlshortenerservice.model.UrlEntity;
import faang.school.urlshortenerservice.properties.RedisProperties;
import faang.school.urlshortenerservice.properties.UrlShortenerProperties;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;

import static java.util.concurrent.TimeUnit.DAYS;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlShortenerProperties urlShortenerProperties;
    private final RedisProperties redisProperties;
    private final RedisTemplate<String, String> redisTemplate;


    @Transactional
    public String shorten(String longUrl) {
        validateUrl(longUrl);

        String hash = hashCache.getHash();

        UrlEntity entity = new UrlEntity();
        entity.setUrlValue(longUrl);
        entity.setHashValue(hash);
        urlRepository.save(entity);

        redisTemplate.opsForValue().set(hash, longUrl, redisProperties.getTtlDays(), DAYS);

        return createShortUrl(hash);
    }

    private void validateUrl(String longUrl) {
        try {
            new URL(longUrl);
        } catch (MalformedURLException e) {
            throw new IncorrectUrl("Incorrect URL: " + longUrl);
        }
    }

    private String createShortUrl(String hash) {
        String protocol = urlShortenerProperties.getProtocol();
        String domain = urlShortenerProperties.getDomain();
        return protocol + "://" + domain + "/" + hash;
    }
}
