package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.entity.RedisUrl;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlMapper mapper;

    public String createShortUrl(LongUrlDto url) {
        String hash = hashCache.getHash();
        Url entity = Url.builder()
            .hash(hash)
            .url(url.getUrl())
            .build();
        urlRepository.save(entity);
        urlCacheRepository.save(mapper.toRedisUrl(entity));
        return hash;
    }

    public String getLongUrl(String hash) {
        Optional<RedisUrl> redisUrlOptional = urlCacheRepository.findByHash(hash);
        if (redisUrlOptional.isPresent()) {
            return redisUrlOptional.get().getHash();
        } else {
            return urlRepository.findUrlByHash(hash)
                .orElseThrow(() -> new RuntimeException("Message")).getUrl();
        }
    }
}
