package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.properties.HashProperties;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exeption.url.UrlNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlCacheRepository urlCacheRepository;
    private final StringRedisTemplate redisTemplate;
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final HashCache hashCache;
    private final HashProperties hashProperties;
    private UrlMapper urlMapper;


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

    @Transactional
    public UrlDto convertLongUrl(Url longUrl) {
        longUrl.setHash(hashCache.getHash());
        redisTemplate.opsForValue().set(longUrl.getHash(), longUrl.getUrl());
        urlCacheRepository.save(longUrl);
        urlRepository.save(longUrl);
        return urlMapper.toDto(longUrl);
    }

    @Transactional
    public List<String> cleanOldUrls() {
        List<String> deletedHashes = urlRepository.deleteOldUrlsAndReturnHashes(hashProperties.getInterval());
        log.info("Удалены хэши старых URL: {}", deletedHashes);

        for (String hash : deletedHashes) {
            Hash hashEntity = hashRepository.findByHash(hash);
            if (hashEntity != null) {
                hashEntity.setUrl(null);
                hashRepository.save(hashEntity);
            }
        }

        return deletedHashes;
    }
}
