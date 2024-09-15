package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    @Value("${server.base-url}")
    private String baseUrl;

    @Value("${app.redis-cache.hours-to-expire}")
    private int hoursToExpire;

    private final HashCache hashCache;
    private final UrlMapper urlMapper;
    private final UrlRepository urlRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public UrlDto makeShortLink(UrlDto urlDto) {
        String hash = hashCache.getHash();

        Url url = urlMapper.toEntity(hash, urlDto.getUrl());
        urlRepository.save(url);
        log.info("{} saved to DB", url);

        redisTemplate.convertAndSend(hash, url.getUrl());
        redisTemplate.expire(hash, hoursToExpire, TimeUnit.HOURS);
        log.info("{} saved to Redis", url);

        return new UrlDto(baseUrl + hash);
    }
}
