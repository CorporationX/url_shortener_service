package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.cache.UrlCacheRepository;
import faang.school.urlshortenerservice.dto.UrlCreateDto;
import faang.school.urlshortenerservice.dto.UrlReadDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {

    @Value("${url.domain}")
    private String domain;

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;
    private final UrlCacheRepository urlCacheRepository;

    public UrlReadDto createShortUrl(UrlCreateDto createDto) {
        Url url = new Url();
        url.setUrl(createDto.getOriginalUrl());
        url.setHash(hashCache.getHash());

        urlRepository.save(url);
        return urlMapper.toDto(url);
    }

    public String getOriginalUrl(String hash) {
        return urlCacheRepository.getUrl(hash).getUrl();
    }

}
