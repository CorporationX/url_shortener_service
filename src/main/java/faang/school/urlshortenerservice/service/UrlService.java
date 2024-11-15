package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.properties.RedisProperties;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;
    private final UrlCacheRepository urlCacheRepository;
    private final RedisProperties redisProperties;

    @Transactional
    public UrlDto createUrlHash(UrlDto urlDto) {
        String hash = hashCache.getHash().toString();

        Url url = new Url();
        url.setUrl(urlDto.getUrl());
        url.setHash(hash);
        url.setCreatedAt(LocalDateTime.now());
        url = urlRepository.save(url);

        urlCacheRepository.saveUrlForTime(hash, url.getUrl(), redisProperties.getTime(), redisProperties.getTimeUnit());

        return urlMapper.toDto(url);
    }
}
