package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.cache.hash.HashCache;
import faang.school.urlshortenerservice.config.properties.redis.RedisProperties;
import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.model.url.Url;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final RedisProperties redisProperties;

    public String saveAndConvertLongUrl(LongUrlDto longUrlDto) {
        String hash = hashCache.getOneHash();
        String longUrl = longUrlDto.getUrl();
        Url urlToConvert = buildUrl(longUrl, hash);
        urlCacheRepository.save(hash, longUrl, redisProperties.getTtl());
        urlRepository.save(urlToConvert);
        return hash;
    }

    private Url buildUrl(String longUrl, String hash) {
        return Url.builder()
                .hash(hash)
                .url(longUrl)
                .build();
    }
}
