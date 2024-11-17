package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotValid;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.properties.RedisProperties;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;
    private final UrlCacheRepository urlCacheRepository;
    private final RedisProperties redisProperties;

    @Transactional
    public UrlDto createUrlHash(UrlDto urlDto) {
        String hash = hashCache.getHash();

        Url urlEntity = new Url();
        urlEntity.setUrl(urlDto.getUrl());
        urlEntity.setHash(hash);
        urlEntity.setCreatedAt(LocalDateTime.now());
        urlEntity = urlRepository.save(urlEntity);
        urlCacheRepository.saveUrlForTime(hash, urlEntity, redisProperties.getTime(), redisProperties.getTimeUnit());
        log.info("Create url hash: " + urlDto.getUrl() + " hash: " + hash);

        return urlMapper.toDto(urlEntity);
    }

    @Transactional
    public String getUrl(String hash) {
        return urlCacheRepository.getUrl(hash)
                .or(() -> urlRepository.findByHash(hash))
                .map(Url::getUrl)
                .orElseThrow(() -> new UrlNotValid("not url exists with hash: " + hash));
    }
}
