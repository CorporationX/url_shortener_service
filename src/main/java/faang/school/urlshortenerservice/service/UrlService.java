package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.NotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    @Value("${server.base-url:http://localhost:8080/}")
    private String baseUrl;

    @Value("${app.redis-cache.hours-to-expire:24}")
    private int hoursToExpire;

    private final HashCache hashCache;
    private final UrlMapper urlMapper;
    private final UrlRepository urlRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public UrlDto makeShortLink(UrlDto urlDto) {
        Optional<Url> urlOptional = urlRepository.findByUrl(urlDto.getUrl());
        if (urlOptional.isPresent()) {
            log.info("Found existing hash");
            return buildUrlDto(urlOptional.get().getHash());
        }

        String hash = hashCache.getHash();
        Url url = urlMapper.toEntity(hash, urlDto.getUrl());

        url = urlRepository.save(url);
        log.info("{} saved to DB", url);

        redisTemplate.opsForValue().set(hash, url.getUrl(), hoursToExpire, TimeUnit.HOURS);
        log.info("{}:{} saved to Redis", url.getHash(), url.getUrl());

        return buildUrlDto(hash);
    }

    public String getOriginalUrl(String hash) {
        String url = redisTemplate.opsForValue().get(hash);
        if (url != null) {
            log.info("Got original url from Redis");
        }
        if (url == null) {
            url = urlRepository.findById(hash)
                    .orElseThrow(() -> new NotFoundException(String.format("Url by hash %s not found!", hash)))
                    .getUrl();
            log.info("Got original url from DB");
        }
        return url;
    }

    private UrlDto buildUrlDto(String hash) {
        return new UrlDto(baseUrl + hash);
    }
}
