package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.cache.UrlCacheRepository;
import faang.school.urlshortenerservice.dto.UrlCreateDto;
import faang.school.urlshortenerservice.dto.UrlReadDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Getter
public class UrlService {
    @Value("${url.domain}")
    private String domain;
    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlMapper urlMapper;

    public UrlReadDto createShortUrl(UrlCreateDto createDto){
        Url url = new Url();
        url.setUrl(createDto.getOriginalUrl());
        url.setHash(hashCache.getHash());
        url = urlCacheRepository.save(url);
        return urlMapper.toDto(url);
    }

    public String getOriginalUrl(String hash) {
        Url url = urlCacheRepository.find(hash);
        return url.getUrl();
    }
}
