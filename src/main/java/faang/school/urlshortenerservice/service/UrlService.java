package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exeption.url.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlCacheRepository urlCacheRepository;
    private final StringRedisTemplate redisTemplate;
    private final UrlRepository urlRepository;


    public Url getOriginalUrl(String hash) {
        String longUrl = redisTemplate.opsForValue().get(hash);
        if (longUrl != null) {
            return new Url(hash, longUrl);
        }

        Url url = urlRepository.getUrlByHash(hash);
        if (url == null) {
            log.error("URL not found for hash: {}", hash);
            throw new UrlNotFoundException("URL not found for hash: " + hash);
        }

        redisTemplate.opsForValue().set(hash, url.getUrl());
        urlCacheRepository.save(url);
        return url;
    }

    public Url convertLongUrl(Url longUrl) {
        // Реализация метода для конвертации длинного URL в хэш
        // Пример: хэшируем длинный URL и сохраняем в кэше
        // String hash = getHash(longUrl.getUrl()); // метода пока нет (увы)
        // longUrl.setHash(hash);

        // Сохраняем в Redis
        // redisTemplate.opsForValue().set(hash, longUrl.getUrl());

        // Сохраняем в UrlCacheRepository
        // urlCacheRepository.save(longUrl);

        return longUrl;
    }
}
