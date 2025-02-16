package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.LocalCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.dto.UrlsDto;
import faang.school.urlshortenerservice.mapper.UrlsDtoMapper;
import faang.school.urlshortenerservice.repository.RedisRepository;
import faang.school.urlshortenerservice.repository.UrlsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlShortenerService {
    private final UrlsDtoMapper urlsDtoMapper;
    private final UrlsRepository urlsRepository;
    private final RedisRepository redisRepository;
    private final LocalCache localCache;

    @Value("${hash.test-url.url-name}")
    private String urlName;

    public UrlDto getShortUrl(String longUrl) {
        log.info("Get an url : {}", longUrl);

        String hash = localCache.getHash();

        urlsRepository.getUrlsJpaRepository().save(
                urlsDtoMapper.toUrls(UrlsDto.builder().url(longUrl).hash(hash).build()));

        redisRepository.setUrl(hash, longUrl);

        return new UrlDto(urlName + hash);
    }

    public UrlDto getLongUrl(String hashCode) {
        log.info("Get by hash : {}", hashCode);

        String urlRedis = redisRepository.getUrl(hashCode);
        if (urlRedis != null){
            log.info("Found in cache, url : {}", urlRedis);
            return UrlDto.builder().url(urlRedis).build();
        }else {
            log.info("Go to DB, hash : {}", hashCode);

            UrlDto urlDto = urlsDtoMapper.toUrlDtoLongUrl(urlsRepository.findByHash(hashCode));
            redisRepository.setUrl(hashCode, urlDto.url());

            return urlDto;
        }
    }
}