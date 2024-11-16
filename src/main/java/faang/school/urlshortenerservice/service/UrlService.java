package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlMapper urlMapper;
    private final UrlRepository urlRepository;
    private final RedisTemplate redisTemplate;
    private final UrlCacheRepository urlCacheRepository;

    public UrlDto createShortUrl(UrlDto urlDto) {
        log.info("start createShortUrl with urlDto: {}", urlDto);

        String hash = hashCache.getHash();
        log.info("get hash: {}", hash);

        Url url = urlRepository.save(urlMapper.toEntity(urlDto, hash));
        log.info("save Url in DB: {}", url);
        saveUrlInRedisCache(url);

        UrlDto shortUrlDto = urlMapper.toDto(url);
        log.info("finish createShortUrl with shortUrl: {}", shortUrlDto);
        return shortUrlDto;
    }

    private void saveUrlInRedisCache(Url url) {
        urlCacheRepository.save(url);
        redisTemplate.expire(url.getHash(), 1, TimeUnit.DAYS);
        log.info("save Url in Redis cache: {}", url);
    }
}
