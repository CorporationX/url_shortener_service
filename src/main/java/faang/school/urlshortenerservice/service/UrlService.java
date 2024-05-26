package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.url.RequestUrlDto;
import faang.school.urlshortenerservice.dto.url.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public UrlDto createShortUrl(RequestUrlDto requestUrlDto) {
        String hash = hashCache.getHash();
        Url url = urlRepository.save(prepareUrl(requestUrlDto, hash));
        return saveUrlInCache(hash, url);
    }
    @Transactional(readOnly = true)
    public UrlDto getFullUrl(String hash) {
        return getUrl(hash);
    }

    private UrlDto saveUrlInCache(String hash, Url url) {
        UrlDto urlDto = UrlDto.builder()
                .hash(url.getHash())
                .url(url.getUrl())
                .build();
        redisTemplate.opsForValue().set(hash, urlDto);
        return urlDto;
    }

    @Transactional(readOnly = true)
    public UrlDto getUrl(String hash) {
        UrlDto urlDto = (UrlDto) redisTemplate.opsForValue().get(hash);
        Url url;
        if (urlDto == null) {
            url = urlRepository.findByHash(hash);
            if (url == null) {
                throw new EntityNotFoundException("Url with hash: %s not found");
            }
            saveUrlInCache(hash, url);
            return urlMapper.toUrlDto(url);
        }
        return urlDto;
    }

    private Url prepareUrl(RequestUrlDto requestUrlDto, String hash) {
        return Url.builder()
                .url(requestUrlDto.getUrl())
                .hash(hash)
                .build();
    }
}
